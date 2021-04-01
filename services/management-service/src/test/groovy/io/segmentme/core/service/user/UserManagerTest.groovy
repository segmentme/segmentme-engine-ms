package io.segmentme.core.service.user

import io.segmentme.core.domain.workpsace.Role
import io.segmentme.core.domain.workpsace.WorkspaceConfiguration
import io.segmentme.core.service.common.BaseTestWithContext
import io.segmentme.helpers.dao.service.UserProfileService
import io.segmentme.helpers.dao.service.WorkspaceService
import io.segmentme.management.service.repository.UserRepository
import io.segmentme.management.service.service.user.UserService
import io.segmentme.management.service.user.UserManager
import org.springframework.beans.factory.annotation.Autowired

import static io.segmentme.core.service.helper.UserHolderHelper.createUser

class UserManagerTest extends BaseTestWithContext {
    @Autowired
    private UserManager userManager;

    @Autowired
    private WorkspaceService workspaceService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserRepository userRepository

    def setup(){
        userRepository.deleteAll()
    }

    def cleanup(){
        userRepository.deleteAll()
    }

    def "User creation should lead to creating minimal viable state"() {
        given:
        def user = createUser()
        when:
        def createdUser = userManager.createUser(user)
        then:
        def userToValidate = userService.findById(createdUser.getId()).get()
        assert userToValidate.getId() == createdUser.getId()

        def profile = userProfileService.getUserProfiles(userToValidate.getId()).get(0)

        assert profile.getRole() == Role.OWNER

        def workspace = workspaceService.findById(profile.getWorkspaceId()).get()
        assert workspace.name == "Default"
        assert workspace.configuration == new WorkspaceConfiguration().setKnownDateFormats(WorkspaceConfiguration.DEFAULT_DATE_PATTERNS)
        assert workspace.integrationPoints.size() == 1
    }
}
