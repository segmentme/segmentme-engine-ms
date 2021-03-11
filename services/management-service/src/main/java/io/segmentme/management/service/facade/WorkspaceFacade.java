package io.segmentme.management.service.facade;

import io.segmentme.core.domain.workpsace.UserProfile;
import io.segmentme.core.domain.workpsace.WorkspaceConfiguration;
import io.segmentme.helpers.context.processor.DateResolver;
import io.segmentme.management.domain.user.User;
import io.segmentme.management.service.dto.WorkspaceHolder;
import io.segmentme.management.service.dto.workspace.WorkspaceDatesValidationRequest;
import io.segmentme.management.service.dto.workspace.WorkspaceDatesValidationResponse;
import io.segmentme.management.service.dto.workspace.WorkspaceDetails;
import io.segmentme.management.service.dto.workspace.WorkspaceUserProfile;
import io.segmentme.management.service.user.UserManager;
import io.segmentme.management.service.workspace.UserProfileManager;
import io.segmentme.management.service.workspace.WorkspaceManager;
import io.segmentme.models.shared.analysis.IntegrationPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class WorkspaceFacade {

    private final WorkspaceManager workspaceManager;

    private final UserProfileManager userProfileManager;

    private final UserManager userManager;

    public WorkspaceDetails getWorkspaceDetails(String workspaceId) {
        return convertToWorkspaceDetails(workspaceManager.getWorkspace(workspaceId));
    }

    public WorkspaceDetails createWorkspace(String ownderId, String name) {
        return convertToWorkspaceDetails(workspaceManager.createWorkspace(ownderId, name));
    }

    private WorkspaceDetails convertToWorkspaceDetails(WorkspaceHolder holder) {
        return new WorkspaceDetails()
                .setId(holder.getId())
                .setName(holder.getName())
                .setIntegrationPoints(holder.getIntegrationPoints())
                .setConfiguration(holder.getWorkspaceConfiguration());
    }

    public IntegrationPoint addIntegrationPoint(String workspaceId, String name) {
        return workspaceManager.addIntegrationPoint(workspaceId, name);

    }

    public void updateConfiguration(String workspaceId, WorkspaceConfiguration workspaceConfiguration) {
        workspaceManager.updateConfiguration(workspaceId, new WorkspaceHolder().setWorkspaceConfiguration(workspaceConfiguration));
    }

    public void updateIntegrationPoint(String workspaceId, IntegrationPoint integrationPoint) {
        workspaceManager.updateIntegrationPoint(workspaceId, integrationPoint);
    }

    public void removeIntegrationPoint(String workspaceId, String key) {
        workspaceManager.removeIntegrationPoint(workspaceId, key);
    }

    public WorkspaceDatesValidationResponse validateDateFormats(WorkspaceDatesValidationRequest validationRequest) {

        Map<String, Optional<DateTimeFormatter>> patterns = validationRequest.getFormats().stream().collect(Collectors.toMap(it -> it, this::silentOfPattern));

        WorkspaceDatesValidationResponse workspaceDatesValidationResponse = new WorkspaceDatesValidationResponse();

        workspaceDatesValidationResponse.setFormats(patterns.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, it -> it.getValue().isPresent())));

        if (!CollectionUtils.isEmpty(validationRequest.getDatesToValidate())) {
            Map<String, DateTimeFormatter> validPatterns = patterns.entrySet().stream().filter(it -> it.getValue().isPresent()).collect(Collectors.toMap(Map.Entry::getKey, it -> it.getValue().get()));
            workspaceDatesValidationResponse.setDates(
                    validationRequest.getDatesToValidate()
                            .stream().collect(HashMap::new, (m, v) -> m.put(v, tryToParseDate(v, validPatterns)), HashMap::putAll)
            );
        }

        return workspaceDatesValidationResponse;
    }

    private String tryToParseDate(String candidate, Map<String, DateTimeFormatter> validPatterns) {
        return validPatterns.entrySet().stream()
                .filter(it -> DateResolver.resolve(candidate, Arrays.asList(it.getValue())).isPresent())
                .map(Map.Entry::getKey).findAny().orElse(null);
    }

    private Optional<DateTimeFormatter> silentOfPattern(String format) {
        try {
            DateTimeFormatter value = DateTimeFormatter.ofPattern(format);
            if (DateResolver.resolve(value.format(ZonedDateTime.now()), Arrays.asList(value)).isPresent()) {
                return Optional.of(value);
            }
            return Optional.empty();
        } catch (RuntimeException ex) {
            log.error("Unable to create date time formatter for pattern {}", format, ex);
        }
        return Optional.empty();
    }

    public void updateWorkspace(String workspaceId, WorkspaceDetails workspaceDetails) {
        workspaceManager.updateConfiguration(workspaceId, new WorkspaceHolder().setName(workspaceDetails.getName()).setWorkspaceConfiguration(workspaceDetails.getConfiguration()));
    }

    public void removeWorkspace(String workspaceId) {
        workspaceManager.removeWorkspace(workspaceId);
    }

    public List<WorkspaceUserProfile> getWorkspaceProfiles(String workspaceId) {
        List<UserProfile> workspaceProfiles = userProfileManager.getWorkspaceProfiles(workspaceId);
        List<String> userIds = workspaceProfiles.stream().map(UserProfile::getUserId).collect(Collectors.toList());
        Map<String, User> users = userManager.getByIds(userIds).stream().collect(Collectors.toMap(User::getId, u -> u));
        return workspaceProfiles.stream().map(profile -> this.convertToWorkspaceProfile(profile, users.get(profile.getUserId()))).collect(Collectors.toList());
    }

    private WorkspaceUserProfile convertToWorkspaceProfile(UserProfile profile, User user) {
        return new WorkspaceUserProfile().setEmail(user.getEmail()).setName(user.getName()).setProfileId(profile.getId()).setUserId(user.getId()).setRole(profile.getRole());
    }
}
