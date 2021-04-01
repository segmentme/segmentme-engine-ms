package io.segmentme.core.service.repository

import io.segmentme.core.domain.context.ContextSchema
import io.segmentme.core.service.common.BaseTestWithContext
import io.segmentme.helpers.dao.repository.ContextSchemaRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

class ContextSchemaRepositoryTest extends BaseTestWithContext {

    @Autowired
    private ContextSchemaRepository repository

    def 'find context schema short form'() {

        given:
        def schemas = [generateSchema(), generateSchema(), generateSchema()]
        schemas = repository.saveAll(schemas)
        when:
        def find = repository.findShortFormByIntegrationPointKeyIn([schemas[0].getIntegrationPointKey(), schemas[1].getIntegrationPointKey()])

        then:
        find.size() == 2
        find.forEach(it -> {
            assert it.getIntegrationPointKey() != null
            assert it.getName() != null
            assert it.getRawPayload() == null
        })
    }

    private ContextSchema generateSchema() {
        new ContextSchema().setName("name").setRawPayload("some raw payload").setIntegrationPointKey(UUID.randomUUID().toString())
    }


}
