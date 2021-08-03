package io.segmentme.core.service.repository

import io.segmentme.core.service.common.BaseTestWithContext
import io.segmentme.core.service.helper.UserHelper
import io.segmentme.core.service.helper.WorkspaceHelper
import io.segmentme.management.service.repository.WorkspaceRepository
import org.springframework.beans.factory.annotation.Autowired

import static io.segmentme.core.service.helper.UserProfileHelper.createProfile

class WorkspaceRepositoryTest extends BaseTestWithContext {

    @Autowired
    private WorkspaceHelper workspaceHelper;

    @Autowired
    private UserHelper userHelper;

    @Autowired
    private WorkspaceRepository repository

    def 'save workspace'() {

        given:
        def user = userHelper.createAndSaveUser();
        def workspace = workspaceHelper.createAndSaveWorkspace()
        workspace.setUserProfiles(Arrays.asList(createProfile(user, workspace)))

        when:
        def save = repository.save(workspace)

        then:
        def found = repository.findById(save.getId()).get()
        found.id == save.id
    }


    def 'test find by integration point key'() {
        when:
        def workspaceToFind = workspaceHelper.createAndSaveWorkspace()
        workspaceHelper.createAndSaveWorkspace()
        then:
        def found = repository.findByIntegrationPointsKey(workspaceToFind.integrationPoints[0].key).get()
        found.id == workspaceToFind.id
    }

}
