package io.segmentme.core.service.service.context

import io.segmentme.core.domain.workpsace.Workspace
import io.segmentme.core.service.configuration.test.ResourceHolder
import io.segmentme.helpers.context.processor.ContextSchemaResolver
import io.segmentme.management.service.context.ContextSchemaValidationServiceImpl
import io.segmentme.models.shared.exception.SeverityLevel
import spock.lang.Specification

import static io.segmentme.helpers.context.processor.helper.WorkspaceConfigurationHelper.defaultWorkspaceConfiguration
import static io.segmentme.management.service.exception.error.ContextValidationErrors.*
import static io.segmentme.models.shared.exception.Errors.ERRORS_SEVERITY

class ContextSchemaValidationServiceImplTest extends Specification {

    static ResourceHolder resourceHolder = new ResourceHolder();
    def contextSchemaResolver = new ContextSchemaResolver()

    def setupSpec() {
        resourceHolder.init();
    }

    def "Of path #path and #code is #expectedSeverity"() {
        expect:
        ContextSchemaValidationServiceImpl.of(path, code).getSeverity() == expectedSeverity

        where:
        path    | code                                                || expectedSeverity
        "root"  | CONTEXT_SCHEMA_SHOULD_CONTAINS_AT_LEAST_ONE_ELEMENT || ERRORS_SEVERITY.get(code)
        "root1" | ROOT_NODE_SHOULD_BE_OBJECT                          || ERRORS_SEVERITY.get(code)
        "root2" | ROOT_NODE_SHOULDNT_HAVE_SUBTUPES                    || ERRORS_SEVERITY.get(code)
        "root3" | NODE_TYPE_NOT_DEFINED                               || ERRORS_SEVERITY.get(code)
        "root4" | NODE_SUBTYPE_NOT_DEFINED                            || ERRORS_SEVERITY.get(code)
        "root5" | NODE_NAME_NOT_DEFINED                               || ERRORS_SEVERITY.get(code)
        "root6" | NODE_SUBTYPE_SHOULD_NOT_BE_DEFINED                  || ERRORS_SEVERITY.get(code)
        "root7" | "UNKNOWN"                                           || SeverityLevel.MID
    }

    def "Test valid json should not contains issues"() {
        given:
        def validationService = new ContextSchemaValidationServiceImpl()

        def schema = contextSchemaResolver.resolve(new Workspace().setConfiguration(defaultWorkspaceConfiguration()), resourceHolder.getValidJsonPayloadConfiguration())
        when:
        def validationResult = validationService.validate(schema);
        then:
        validationResult.isEmpty()
    }

    def "Test invalid json should  contains critical issues"() {
        given:
        def validationService = new ContextSchemaValidationServiceImpl()
        def schema = contextSchemaResolver.resolve(new Workspace().setConfiguration(defaultWorkspaceConfiguration()), resourceHolder.getInvalidJsonPayloadConfiguration())
        when:
        def validationResult = validationService.validate(schema);
        then:
        !validationResult.findAll { it -> it.severity == SeverityLevel.CRITICAL }
                .findAll { it -> it.code == NODE_SUBTYPE_NOT_DEFINED }.isEmpty()
    }
}
