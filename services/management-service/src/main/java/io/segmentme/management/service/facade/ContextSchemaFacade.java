package io.segmentme.management.service.facade;

import com.fasterxml.jackson.databind.JsonNode;
import io.segmentme.core.domain.context.SchemaNode;
import io.segmentme.core.domain.workpsace.Workspace;
import io.segmentme.helpers.dao.service.WorkspaceService;
import io.segmentme.management.service.context.ContextSchemaManager;
import io.segmentme.management.service.dto.context.*;
import io.segmentme.management.service.exception.ContextSchemaManagerException;
import io.segmentme.models.shared.analysis.InlineType;
import io.segmentme.models.shared.analysis.SchemaNodeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.segmentme.management.service.exception.error.ContextMangerErrors.INTEGRATION_POINT_NOT_FOUND;
import static io.segmentme.management.service.exception.error.ContextValidationErrors.NODE_SUBTYPE_NOT_DEFINED;
import static io.segmentme.management.service.exception.error.ContextValidationErrors.NODE_TYPE_NOT_DEFINED;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContextSchemaFacade {
    private final ContextSchemaManager contextSchemaManager;

    private final WorkspaceService workspaceService;

    @Qualifier("measurement-service")
    private final WebClient measurementServiceClient;


    public List<ContextSchemaBasicInfo> getByWorkspace(String workspaceId, boolean shortForm) {
        return contextSchemaManager.getAllByWorkspaceId(workspaceId, shortForm).stream()
            .map(this::convertToBasicDto).collect(Collectors.toList());
    }

    public ContextSchemaResolveResult resolve(String workspaceId, JsonNode payload) {
        ContextSchemaHolder contextSchema = contextSchemaManager.resolveContextSchema(workspaceId, payload);
        return new ContextSchemaResolveResult()
            .setContextSchema(this.convertToFullDetailsDto(contextSchema))
            .setValidationEntries(contextSchemaManager.validate(contextSchema));
    }


    public ContextSchemaValidationResult validate(String workspaceId, ContextSchemaValidationRequest request) {
        ContextSchemaHolder updatedSchema = contextSchemaManager.resolveContextSchema(request.getRootNode());
        if (request.getRawPayload() == null || request.getRawPayload().isNull()) {
            return new ContextSchemaValidationResult().setValidationEntries(contextSchemaManager.validate(updatedSchema));
        }
        ContextSchemaResolveResult originalSchema = this.resolve(workspaceId, request.getRawPayload());


        if (CollectionUtils.isNotEmpty(originalSchema.getValidationEntries())) {
            originalSchema.getContextSchema()
                .setInlinePath(originalSchema.getContextSchema().getInlinePath()
                    .entrySet().stream()
                    .filter(it -> updatedSchema.getInlinePath().containsKey(it.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            originalSchema.getValidationEntries().removeIf(it -> {
                InlineType inlineType = updatedSchema.getInlinePath().get(it.getPath());
                if (inlineType == null) {
                    return true;
                } else if (it.getCode().equalsIgnoreCase(NODE_SUBTYPE_NOT_DEFINED)) {
                    return inlineType.getSubType() != null;
                } else if (it.getCode().equalsIgnoreCase(NODE_TYPE_NOT_DEFINED)) {
                    return inlineType.getRootType() != null;
                }
                return false;
            });
        }
        return new ContextSchemaValidationResult().setValidationEntries(originalSchema.getValidationEntries());
    }

    public ContextSchemaBasicInfo create(ContextSchemaCreateRequest request) {
        ContextSchemaHolder contextSchemaHolder = contextSchemaManager.create(request.getIntegrationPointKey(), request.getRootNode(), request.getName(), request.getRawPayload(), request.getUniquenessIndicator());
        return this.convertToBasicDto(contextSchemaHolder);
    }

    public void delete(String contextSchemaId) {
        contextSchemaManager.deleteContextSchema(contextSchemaId);
    }

    public ContextSchemaFullDetails getById(String contextSchemaId) {
        return convertToFullDetailsDto(contextSchemaManager.getById(contextSchemaId));
    }

    private ContextSchemaFullDetails convertToFullDetailsDto(ContextSchemaHolder holder) {
        return (ContextSchemaFullDetails) new ContextSchemaFullDetails()
            .setRootNode(holder.getRootNode())
            .setRawPayload(holder.getRawPayload())
            .setNodeValues(holder.getNodeValues()).setInlinePath(holder.getInlinePath())
            .setUniquenessIndicator(holder.getUniquenessIndicator())
            .setId(holder.getId())
            .setIntegrationPointKey(holder.getIntegrationPointKey())
            .setHash(holder.getHash())
            .setName(holder.getName());
    }

    public ContextSchemaBasicInfo update(String contextId, ContextSchemaUpdateRequest payload) {
        return convertToBasicDto(contextSchemaManager.updateContextSchema(contextId, new ContextSchemaHolder()
            .setName(payload.getName())
            .setUniquenessIndicator(payload.getUniquenessIndicator())
            .setIntegrationPointKey(payload.getIntegrationPointKey())
            .setRootNode(payload.getRootNode())));
    }


    private ContextSchemaBasicInfo convertToBasicDto(ContextSchemaHolder contextSchema) {
        return (ContextSchemaBasicInfo) new ContextSchemaBasicInfo()
            .setUniquenessIndicator(contextSchema.getUniquenessIndicator())
            .setInlinePath(contextSchema.getInlinePath())
            .setHash(contextSchema.getHash())
            .setIntegrationPointKey(contextSchema.getIntegrationPointKey())
            .setName(contextSchema.getName())
            .setId(contextSchema.getId());
    }


    public ContextSchemaShortInfo actualizeSchema(ContextSchemaActualizeRequest payload) {
        String integrationPointKey = payload.getIntegrationPointId();
        Workspace workspace = workspaceService.findByIntegrationPointKey(integrationPointKey).orElseThrow(() -> new ContextSchemaManagerException().setCode(INTEGRATION_POINT_NOT_FOUND));
        ContextSchemaHolder resolvedSchema = contextSchemaManager.resolveContextSchema(workspace, payload.getPayload());
        if (resolvedSchema.getInlinePath().entrySet().stream().anyMatch(it -> it.getValue().getRootType() == SchemaNodeType.UNDEFINED || it.getValue().getSubType() == SchemaNodeType.UNDEFINED)) {
            log.warn("Integration point key : {} Schema {} contains undefined values", integrationPointKey, payload.getPayload());
        }


        String hash = StringUtils.isNoneBlank(payload.getContextKey()) ? payload.getContextKey() : contextSchemaManager.computeHash(resolvedSchema);
        resolvedSchema.setHash(hash);
        ContextSchemaHolder existedSchema = contextSchemaManager.findByHash(integrationPointKey, hash);
        ContextSchemaHolder actualizedContext;
        String payloadAsString = payload.getPayload().toString();

        if (existedSchema == null) {
            actualizedContext = contextSchemaManager.create(integrationPointKey, resolvedSchema.getRootNode(), payload.getContextKey(), payloadAsString, hash);
        } else {
            resolvedSchema.setName(existedSchema.getName());
            resolvedSchema.setIntegrationPointKey(integrationPointKey);
            resolvedSchema.setRawPayload(payloadAsString);
            resolveUnknownProperties(resolvedSchema, existedSchema);
            actualizedContext = contextSchemaManager.updateContextSchema(existedSchema.getId(), resolvedSchema);
        }
        if (StringUtils.isNoneBlank(existedSchema.getUniquenessIndicator())) {
            acknowledgeContextParticipant(existedSchema);
        }
        return new ContextSchemaShortInfo().setId(actualizedContext.getId()).setIntegrationPointKey(integrationPointKey).setHash(actualizedContext.getHash());
    }

    private void resolveUnknownProperties(ContextSchemaHolder resolvedSchema, ContextSchemaHolder existedSchema) {
        Map<String, InlineType> resolvedUndefinedPaths = resolvedSchema.getInlinePath().entrySet().stream().filter(it -> it.getValue().getRootType() == SchemaNodeType.UNDEFINED || it.getValue().getSubType() == SchemaNodeType.UNDEFINED)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        resolvedUndefinedPaths.entrySet().stream().filter(it -> wasResolvedBefore(it.getKey(), existedSchema.getInlinePath())).forEach(it -> updateType(it.getKey(), resolvedSchema, existedSchema));
    }

    private void updateType(String key, ContextSchemaHolder resolvedSchema, ContextSchemaHolder existedSchema) {
        InlineType actualType = existedSchema.getInlinePath().get(key);
        resolvedSchema.getInlinePath().put(key, actualType);

        SchemaNode nodeToUpdate = getNode(resolvedSchema.getRootNode(), key);
        if (nodeToUpdate == null) {
            return;
        }
        nodeToUpdate.setType(actualType.getRootType());
        nodeToUpdate.setSubType(actualType.getSubType());

    }

    private SchemaNode getNode(SchemaNode rootNode, String key) {
        if (StringUtils.isBlank(rootNode.getPath()) ||
            (!rootNode.getPath().equalsIgnoreCase(key)) && CollectionUtils.isNotEmpty(rootNode.getSubNodes())) {
            return rootNode.getSubNodes().stream().map(it -> getNode(it, key)).filter(Objects::nonNull).findFirst().orElse(null);
        }

        if (rootNode.getPath().equalsIgnoreCase(key)) {
            return rootNode;
        }

        return null;
    }

    private boolean wasResolvedBefore(String key, Map<String, InlineType> inlinePath) {
        InlineType inlineType = inlinePath.get(key);
        return inlineType.getRootType() != SchemaNodeType.UNDEFINED || inlineType.getSubType() != SchemaNodeType.UNDEFINED;
    }


    private void acknowledgeContextParticipant(ContextSchemaHolder contextSchemaHolder) {
        measurementServiceClient.post()
            .uri("/participant")
            .bodyValue(contextSchemaHolder)
            .retrieve()
            .bodyToMono(Void.class)
            .block();
    }
}
