package io.segmentme.analysis

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.segmentme.analysis.domain.context.ContextSchema
import io.segmentme.analysis.domain.context.SchemaNode
import io.segmentme.analysis.domain.segment.Segment
import io.segmentme.analysis.dto.SegmentAnalysisResult
import io.segmentme.analysis.helper.AnalysisResourceHolder
import io.segmentme.analysis.repository.SegmentRepository
import io.segmentme.analysis.service.segment.AnalysisService
import io.segmentme.helpers.context.processor.ContextValueHolder
import io.segmentme.helpers.context.processor.ContextValuesExtractorImpl
import io.segmentme.redis.config.MessagePublisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.Resource
import org.springframework.data.redis.core.RedisTemplate

import static io.segmentme.helpers.context.processor.helper.ExtractorConfigurationHelper.defaultWorkspaceConfiguration

abstract class BaseRuleTest extends BaseTestWithContext {

    @Value("classpath:rules/schema.json")
    protected Resource schema

    @Autowired
    protected AnalysisResourceHolder resourceHolder

    @MockBean
    protected MessagePublisher messagePublisher

    @MockBean
    protected RedisTemplate<String, Object> redisTemplate;

    @Autowired
    protected AnalysisService analysisService

    @Autowired
    protected ObjectMapper objectMapper

    @Autowired
    protected SegmentRepository analysisRuleRepository;

    private ContextValueHolder context

    def setup() {
        def json = objectMapper.readValue(schema.getInputStream(), JsonNode.class)
        ContextSchema contextSchema = new ContextSchema()
        contextSchema.setRootNode(new SchemaNode())
        context = ContextValuesExtractorImpl.INSTANCE.extractValues(json, contextSchema, defaultWorkspaceConfiguration())
    }

    protected static SegmentAnalysisResult resultValue(String name, List<SegmentAnalysisResult> results) {
        return results.stream()
            .filter(it -> it.getName().contains(name))
            .findFirst()
            .orElse(null)
    }

    def getContext() {
        return this.context
    }

    protected saveAllSegments() {
        analysisRuleRepository.saveAll(resourceHolder.getSegments())
    }

    protected deleteAllSegments() {
        analysisRuleRepository.deleteAll()
    }

    protected Segment getSegmentsByName(String name) {
        return analysisRuleRepository.findAll().stream().filter(it -> it.getName().equals(name)).findFirst().orElse(null)
    }


    static boolean match(Segment segment, String segmentName) {
        return segment.getName().equalsIgnoreCase(segmentName)
    }
}


