package io.segmentme.core.service


import io.segmentme.helpers.context.processor.helper.ContextProcessorResourceHolder
import io.segmentme.management.service.domain.workpsace.Workspace
import io.segmentme.management.service.service.context.ContextSchemaResolver
import io.segmentme.models.shared.analysis.SchemaNodeType
import spock.lang.Specification

import static io.segmentme.core.service.helper.WorkspaceConfigurationHelper.defaultWorkspaceConfiguration
import static io.segmentme.models.shared.analysis.InlineType.of

class ContextSchemaResolverTest extends Specification {


    static ContextProcessorResourceHolder resourceHolder = new ContextProcessorResourceHolder();
    public static final ContextSchemaResolver resolver = new ContextSchemaResolver()


    def setupSpec() {
        resourceHolder.init();
    }

    def "Test node counts should match expected size"() {
        given:
        def schema = resolver.resolve(new Workspace().setConfiguration(defaultWorkspaceConfiguration()), resourceHolder.getValidJsonPayloadConfiguration())
        expect:
        schema.getInlinePath().size() == 24
    }


    def "Valid JSON Check that node #nodeName is  #type"() {
        given:
        def schema = resolver.resolve(new Workspace().setConfiguration(defaultWorkspaceConfiguration()), resourceHolder.getValidJsonPayloadConfiguration())
        expect:
        schema.getInlinePath().get(nodeName) == type
        where:
        nodeName                              || type
        "user"                                || of(SchemaNodeType.OBJECT, null)
        "user.numbersArray"                   || of(SchemaNodeType.ARRAY, SchemaNodeType.NUMBER)
        "user.email"                          || of(SchemaNodeType.STRING, null)
        "user.name"                           || of(SchemaNodeType.STRING, null)
        "user.details"                        || of(SchemaNodeType.OBJECT, null)
        "user.details.gender"                 || of(SchemaNodeType.STRING, null)
        "user.details.address"                || of(SchemaNodeType.OBJECT, null)
        "user.details.address.addressLine1"   || of(SchemaNodeType.STRING, null)
        "user.details.address.state"          || of(SchemaNodeType.STRING, null)
        "user.details.birthDate"              || of(SchemaNodeType.DATE, null)
        "user.details.phone"                  || of(SchemaNodeType.STRING, null)
        "stringArray"                         || of(SchemaNodeType.ARRAY, SchemaNodeType.STRING)
        "objectArrays"                        || of(SchemaNodeType.ARRAY, SchemaNodeType.OBJECT)
        "objectArrays.id"                     || of(SchemaNodeType.STRING, null)
        "objectArrays.agreementNumber"        || of(SchemaNodeType.NUMBER, null)
        "objectArrays.subObjects"             || of(SchemaNodeType.ARRAY, SchemaNodeType.OBJECT)
        "objectArrays.subObjects.array"       || of(SchemaNodeType.ARRAY, SchemaNodeType.STRING)
        "objectArrays.subObjects.subObjectId" || of(SchemaNodeType.STRING, null)
        "objectArrays.numbersArray"           || of(SchemaNodeType.ARRAY, SchemaNodeType.NUMBER)
        "objectArrays.isActive"               || of(SchemaNodeType.BOOLEAN, null)
        "objectArrays.dateTime"               || of(SchemaNodeType.DATE, null)
        "user.status"                         || of(SchemaNodeType.STRING, null)
        "user.fullAge"                        || of(SchemaNodeType.NUMBER, null)
        "user.weight"                         || of(SchemaNodeType.NUMBER, null)
    }

    def "Invalid JSON Check node #nodeName is  #type"() {
        given:
        def schema = resolver.resolve(new Workspace().setConfiguration(defaultWorkspaceConfiguration()), resourceHolder.getInvalidJsonPayloadConfiguration())
        expect:
        schema.getInlinePath().get(nodeName) == type
        where:
        nodeName                       || type
        "objectArrays"                 || of(SchemaNodeType.ARRAY, SchemaNodeType.UNDEFINED)
        "objectArrays.id"              || of(SchemaNodeType.STRING, null)
        "objectArrays.agreementNumber" || of(SchemaNodeType.NUMBER, null)
        "objectArrays.isActive"        || of(SchemaNodeType.BOOLEAN, null)
        "objectArrays.dateTime"        || of(SchemaNodeType.DATE, null)
    }
}
