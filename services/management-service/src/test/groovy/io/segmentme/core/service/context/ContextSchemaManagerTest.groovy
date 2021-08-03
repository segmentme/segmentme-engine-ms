package io.segmentme.core.service.context

import io.segmentme.core.service.common.BaseTestWithContext
import io.segmentme.core.service.helper.UserHolderHelper
import io.segmentme.management.service.domain.context.SchemaNode
import io.segmentme.management.service.domain.workpsace.Workspace
import io.segmentme.management.service.exception.ContextSchemaManagerException
import io.segmentme.management.service.exception.error.ContextMangerErrors
import io.segmentme.management.service.repository.UserRepository
import io.segmentme.management.service.repository.WorkspaceRepository
import io.segmentme.management.service.service.context.ContextSchemaManager
import io.segmentme.models.shared.analysis.SchemaNodeType
import io.segmentme.models.shared.exception.AbstractManagerException
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Shared

class ContextSchemaManagerTest extends BaseTestWithContext {

    public static final String UNKNOWN_INTEGRATION_POINT = "unknown-integration-point"
    public static final SchemaNode validSchema = new SchemaNode().setName("ROOT").setType(SchemaNodeType.OBJECT).setSubNodes(Arrays.asList(new SchemaNode().setName("name").setType(SchemaNodeType.NUMBER)))

    @Autowired
    private UserHolderHelper userHelper

    @Autowired
    private WorkspaceRepository workspaceRepository

    @Autowired
    private ContextSchemaManager contextSchemaManager

    @Autowired
    private UserRepository userRepository;

    @Shared
    List<Workspace> workspaces = new ArrayList<>()

    def cleanup() {
        userRepository.deleteAll()
    }

    def "Create context #rootNode for integration key #integrationKey should #result"() {
        setup:
        def user = userHelper.createUserAndState();
        workspaces = workspaceRepository.findAll().findAll { it -> it.userProfiles.any { profile -> profile.userId == user.id } }
        expect:
        try {
            def create = contextSchemaManager.create(integrationKey ?: workspaces[0].integrationPoints[0].key, rootNode, null, null, null)
            assert create != null && result == true
        } catch (AbstractManagerException ex) {
            assert ex == result
        }
        where:
        rootNode                                                        | integrationKey            || result
        validSchema                                                     | null                      || true
        validSchema                                                     | UNKNOWN_INTEGRATION_POINT || new ContextSchemaManagerException().setCode(ContextMangerErrors.INTEGRATION_POINT_NOT_FOUND)
        new SchemaNode().setName("root").setType(SchemaNodeType.OBJECT) | null                      || true


    }

    def "UpdateContextSchema"() {
    }

}
