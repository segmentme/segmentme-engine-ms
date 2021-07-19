package io.segmentme.measurement

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.segment.security.mock.AccessServiceMock
import io.segmentme.analysis.dto.CollectedAnalysysStatisticDto
import io.segmentme.analysis.dto.SegmentAnalysisResult
import io.segmentme.analysis.dto.segment.SegmentDto
import io.segmentme.measurement.dto.ParticipantAcknowledgeRequest
import io.segmentme.measurement.repository.ContextStatisticsRepository
import io.segmentme.measurement.repository.ParticipantsStatisticRepository
import io.segmentme.measurement.repository.StatisticRepository
import io.segmentme.measurement.service.MeasurementService
import io.segmentme.measurement.service.ParticipantStatisticService
import io.segmentme.measurement.service.StatisticManager
import io.segmentme.measurement.service.StatisticService
import io.segmentme.measurement.test.configuration.TestData
import io.segmentme.measurement.test.configuration.TestUser
import io.segmentme.models.shared.analysis.InlineType
import io.segmentme.models.shared.analysis.SchemaNodeType
import org.apache.commons.lang3.RandomUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@SpringBootTest(classes = [TestConfiguration.class])
class MeasurementServiceTest extends AccessServiceMock {

    public static final String CONTEXT_ID = "contextId"
    public static final Integer EXPECTED_PERCENTAGE = 20
    public static final String SEGMENT_ID_TO_OPEN = "3"
    public static final String UNIQUENESS_INDICATOR = "user.id"
    public static final String UNIQUENESS_INDICATOR2 = "user.email"
    public static final String CLIENT_ID = "clientId"
    public static final String IP_KEY = UUID.randomUUID().toString()
    public static final String WORKSPACE_ID = UUID.randomUUID().toString()
    @Autowired
    private StatisticRepository repository
    @Autowired
    private StatisticService statisticService;

    @Autowired
    private StatisticManager statisticManager;

    @Autowired
    private MeasurementService measurementService;

    @Autowired
    private ParticipantStatisticService participantStatisticService;

    @Autowired
    private ParticipantsStatisticRepository participantsStatisticRepository;
    @Autowired
    private ContextStatisticsRepository contextStatisticsRepository;


    @Autowired
    private ObjectMapper objectMapper;

    def "RefreshParticipants"() {

        TestData testData = null
        def statsLogs = (0..50).collect {
            {

                    if (it % 4 == 0) {
                        testData = createTestData()
//                        participantStatisticService.acknowledgeParticipant(generateAnalysisStatisticEntry(testData).getContextDataHolder())
                    }
                    CollectedAnalysysStatisticDto collectedAnalysysStatisticDto = generateAnalysisStatisticEntry(testData)
//                    lastParticipantAnalyse.put(testData.getUser().getId(), collectedAnalysysStatisticDto)
                    return collectedAnalysysStatisticDto
            }
        }

        def pool = Executors.newFixedThreadPool(3);
        statsLogs.forEach(it -> statisticManager.saveStatistic(it))


        try {
            pool.awaitTermination(5, TimeUnit.SECONDS)
        } catch (Exception ex) {
            //mute
        }
        statisticManager.flush()

        when:

        measurementService.refreshParticipants(CONTEXT_ID, UNIQUENESS_INDICATOR2)

        then:
        assert participantsStatisticRepository.findAll() != [];

    }

    def 'Redistribute opened segments percentage'() {
        given:
        Map<String, CollectedAnalysysStatisticDto> lastParticipantAnalyse = new HashMap<>()
        TestData testData = null
        def statsLogs = (0..50).collect {
            {

                if (it % 4 == 0) {
                    testData = createTestData()
                    participantStatisticService.acknowledgeParticipant(generateParticipantAcknowledge(testData))
                }
                CollectedAnalysysStatisticDto collectedAnalysysStatisticDto = generateAnalysisStatisticEntry(testData)
                lastParticipantAnalyse.put(testData.getUser().getId(), collectedAnalysysStatisticDto)
                return collectedAnalysysStatisticDto
            }
        }

        when:
        def pool = Executors.newFixedThreadPool(3);
        statsLogs.forEach(it -> statisticManager.saveStatistic(it))


        try {
            pool.awaitTermination(10, TimeUnit.SECONDS)
        } catch (Exception ex) {
            //mute
        }

        measurementService.redistributePercentage(CONTEXT_ID, SEGMENT_ID_TO_OPEN, EXPECTED_PERCENTAGE)
        then:
        def contextStatistic = contextStatisticsRepository.findByContextId(CONTEXT_ID)
        assert contextStatistic.getTotalParticipants() == lastParticipantAnalyse.keySet().size()
        def allParticipants = participantsStatisticRepository.findAll();
        def count = allParticipants.count { it.inSegment.contains(SEGMENT_ID_TO_OPEN) }
        assert count == (int) (EXPECTED_PERCENTAGE * lastParticipantAnalyse.keySet().size() / 100)
        lastParticipantAnalyse.forEach((k, v) -> {
            def savedParticipant = allParticipants.find { it -> it.getUniquenessValue().equals(k) }
            assert savedParticipant != null
            v.segmentAnalysisResults.forEach(expectedSegmentResult -> {
                def savedSegmentResult = savedParticipant.lastSegmentStatistic.find { lastSegment -> lastSegment.getSegmentId() == expectedSegmentResult.segmentId }
                assert expectedSegmentResult.isValue() == savedSegmentResult.isAnalysisResult()
            })
        })
    }


    private ParticipantAcknowledgeRequest generateParticipantAcknowledge(TestData testData) {
        ParticipantAcknowledgeRequest participantAcknowledgeRequest = new ParticipantAcknowledgeRequest()
        participantAcknowledgeRequest.setContextId(CONTEXT_ID)
        participantAcknowledgeRequest.setUniquenessIndicator(UNIQUENESS_INDICATOR)
        participantAcknowledgeRequest.setValues([(UNIQUENESS_INDICATOR): testData.user.id,(UNIQUENESS_INDICATOR2): testData.user.email])

        return participantAcknowledgeRequest;
    }

    private CollectedAnalysysStatisticDto generateAnalysisStatisticEntry(TestData testData) {
        CollectedAnalysysStatisticDto collectedAnalysysStatisticDto = new CollectedAnalysysStatisticDto()
        collectedAnalysysStatisticDto.setClientId(CLIENT_ID)
        collectedAnalysysStatisticDto.setRawPayload(objectMapper.readValue(objectMapper.writeValueAsString(testData), JsonNode.class))
        collectedAnalysysStatisticDto.setIntegrationPointKey(IP_KEY)
        collectedAnalysysStatisticDto.setWorkspaceId(WORKSPACE_ID)
        collectedAnalysysStatisticDto.setSegmentAnalysisResults(generateSegmentAnalysisResult())
        CollectedAnalysysStatisticDto.ContextDataHolder contextDataHolder = new CollectedAnalysysStatisticDto.ContextDataHolder();
        contextDataHolder.setContextId(CONTEXT_ID)
        contextDataHolder.setUniquenessIndicator(UNIQUENESS_INDICATOR)
        contextDataHolder.setValues([(UNIQUENESS_INDICATOR): testData.user.id,(UNIQUENESS_INDICATOR2): testData.user.email])
        contextDataHolder.setKnownTypes([(UNIQUENESS_INDICATOR): InlineType.of(SchemaNodeType.STRING, null)])
        contextDataHolder.setExtractedValues([(UNIQUENESS_INDICATOR): testData.user.id])
        collectedAnalysysStatisticDto.setContextDataHolder(contextDataHolder)
        collectedAnalysysStatisticDto.setAnalyzedSegments(prepareAnalyzedSegments(collectedAnalysysStatisticDto.getSegmentAnalysisResults()));
        collectedAnalysysStatisticDto.setTimestamp(System.currentTimeMillis())
        return collectedAnalysysStatisticDto;
    }

    List<SegmentDto> prepareAnalyzedSegments(List<SegmentAnalysisResult> segmentAnalysisResults) {
        return segmentAnalysisResults.collect { it -> new SegmentDto().setHash(it.getHash()).setConditions([]).setId(it.getSegmentId()).setActive(true) }
    }

    List<SegmentAnalysisResult> generateSegmentAnalysisResult() {
        List<SegmentAnalysisResult> segAnalysisResults = []
        (0..10).forEach(it -> {
            segAnalysisResults.add(new SegmentAnalysisResult().setSegmentId(String.valueOf(it)).setValue(RandomUtils.nextBoolean()).setHash(String.valueOf(it)))
        })
        return segAnalysisResults
    }

    public TestData createTestData() {
        def testUser = new TestUser()
        testUser.id = UUID.randomUUID().toString()
        testUser.email = UUID.randomUUID().toString()

        def data = new TestData()
        data.user = testUser
        return data
    }


}
