package io.segmentme.analysis

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.segmentme.analysis.dto.SegmentAnalysisResult
import io.segmentme.analysis.helper.ResourceHolder
import io.segmentme.analysis.service.segment.AnalysisService
import io.segmentme.core.domain.segment.Segment
import io.segmentme.core.domain.workpsace.Workspace
import io.segmentme.helpers.context.processor.ContextSchemaResolver
import io.segmentme.helpers.context.processor.ContextValueHolder
import io.segmentme.helpers.context.processor.ContextValuesExtractorImpl
import io.segmentme.helpers.dao.repository.SegmentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource

import static io.segmentme.helpers.context.processor.helper.WorkspaceConfigurationHelper.defaultWorkspaceConfiguration

abstract class BaseRuleTest extends BaseTestWithContext {

    @Value("classpath:rules/schema.json")
    protected Resource schema

    @Autowired
    protected ResourceHolder resourceHolder

    @Autowired
    protected AnalysisService analysisService

    @Autowired
    protected ObjectMapper objectMapper

    @Autowired
    private ContextValuesExtractorImpl contextValuesExtractor

    @Autowired
    private ContextSchemaResolver contextSchemaResolver

    @Autowired
    protected SegmentRepository analysisRuleRepository;

    private ContextValueHolder context

    def setup() {
        def json = objectMapper.readValue(schema.getInputStream(), JsonNode.class)
        context = contextValuesExtractor.extractValues(json, contextSchemaResolver.resolve(new Workspace().setConfiguration(defaultWorkspaceConfiguration()), json), defaultWorkspaceConfiguration())
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


