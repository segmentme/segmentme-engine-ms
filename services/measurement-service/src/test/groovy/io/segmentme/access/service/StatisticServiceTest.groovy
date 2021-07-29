package io.segmentme.access.service

import io.segment.security.mock.AccessServiceMock
import io.segmentme.access.service.domain.statistic.ParticipantStatistic
import io.segmentme.access.service.repository.ContextStatisticsRepository
import io.segmentme.access.service.repository.ParticipantsStatisticRepository
import io.segmentme.access.service.repository.StatisticRepository
import io.segmentme.access.service.service.ParticipantStatisticService
import io.segmentme.access.service.service.StatisticService
import io.segmentme.analysis.domain.statistic.StatisticLog
import org.apache.commons.lang3.RandomUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

import java.time.Instant
import java.time.temporal.ChronoUnit

@SpringBootTest(classes = [TestConfiguration.class])
//@ActiveProfiles("test")
class StatisticServiceTest extends AccessServiceMock {

    public static final String CONTEXT_ID = "contextId"
    public static final String UNIQUENESS_INDICATOR = "user.id"
    public static final String CLIENT_ID = "clientId"
    public static final String SEGMENT_ID = "segmentId"
    @Autowired
    private StatisticRepository repository
    @Autowired
    private StatisticService statisticService;
    @Autowired
    private ParticipantStatisticService participantStatisticService;

    @Autowired
    private ContextStatisticsRepository contextStatisticsRepository;

    @Autowired
    private ParticipantsStatisticRepository participantsStatisticRepository;

    def 'Include users to segment'() {
        given:
        List<ParticipantStatistic> participantStatistic = (0..10).collect { it -> createParticipantStatistic(RandomUtils.nextBoolean()) }
        participantsStatisticRepository.saveAll(participantStatistic);
        participantStatistic.sort(Comparator.comparing((ParticipantStatistic p) -> p.getLastModifiedDate()).reversed())
        def expectedToInclude = participantStatistic.findAll { it -> it.getLastSegmentStatistic().get(0).analysisResult }.collect().subList(0, 3)
        when:
        participantStatisticService.includeParticipantIntoSegment(3, SEGMENT_ID, CONTEXT_ID)
        then:
        participantsStatisticRepository.findAll().forEach(saved -> {
            if (expectedToInclude.find { expected -> expected.getUniquenessValue() == saved.getUniquenessValue() }) {
                assert saved.inSegment.contains(SEGMENT_ID);
            } else {
                assert !saved.inSegment.contains(SEGMENT_ID);
            }
        })


    }

    def 'Exclude users from segment'() {
        given:
        List<ParticipantStatistic> participantStatistic = (0..10).collect { it -> createParticipantStatistic(RandomUtils.nextBoolean()) }
        participantStatistic.forEach(participant -> {
                if (participant.getLastSegmentStatistic().get(0).isAnalysisResult()) {
                    participant.setInSegment(Arrays.asList(SEGMENT_ID))
                }
            }
            )

        participantsStatisticRepository.saveAll(participantStatistic);
        participantStatistic.sort(Comparator.comparing((ParticipantStatistic p) -> p.getLastModifiedDate()))
        def expectedToInclude = participantStatistic.findAll { it -> it.getLastSegmentStatistic().get(0).analysisResult }.collect().subList(0, 3)
        when:
        participantStatisticService.excludeParticipantFromSegment(3, SEGMENT_ID, CONTEXT_ID)
        then:
        participantsStatisticRepository.findAll().forEach(saved -> {
            if (expectedToInclude.find { expected -> expected.getUniquenessValue() == saved.getUniquenessValue() }) {
                assert !saved.inSegment.contains(SEGMENT_ID);
            } else {
                assert participantStatistic.find {it->it.getUniquenessValue()==saved.getUniquenessValue()}.inSegment.containsAll(saved.getInSegment())
            }
        })


    }

    private ParticipantStatistic createParticipantStatistic(boolean segResult) {
        ParticipantStatistic participantStatistic = new ParticipantStatistic()
        participantStatistic.setUniquenessIndicator(UNIQUENESS_INDICATOR)
        participantStatistic.setLastSegmentStatistic([new StatisticLog.SegmentStatistic(segmentId: SEGMENT_ID, setAnalysisResult: segResult)]);
        participantStatistic.setContextId(CONTEXT_ID)
        participantStatistic.setUniquenessValue(UUID.randomUUID().toString());
        participantStatistic.setLastModifiedDate(Instant.now())
        participantStatistic.setCreatedDate(Instant.now())
    }

    private Integer generateRandomInteger() {
        def nextInt = RandomUtils.nextInt(0, 2)
        return RandomUtils.nextBoolean() ? nextInt : 0 - nextInt
    }

    def 'get segment count result '() {
        given:
        def datesToSave = [] as List<StatisticLog>
        String workspaceId = UUID.randomUUID().toString()
        for (int i = 0; i < 200; i++) {
            datesToSave.add(new StatisticLog().setWorkspaceId(workspaceId).setSegmentStatistics(Arrays.asList(new StatisticLog.SegmentStatistic().setSegmentId("a").setAnalysisResult(true))).setAnalysisTime(RandomUtils.nextLong()))
            datesToSave.add(new StatisticLog().setWorkspaceId(workspaceId).setSegmentStatistics(Arrays.asList(new StatisticLog.SegmentStatistic().setSegmentId("b").setAnalysisResult(true))).setAnalysisTime(RandomUtils.nextLong()))
        }
        repository.saveAll(datesToSave)
        for (int i = 0; i < 200; i++) {
            datesToSave[i].setCreatedDate(Instant.now().minus(i, ChronoUnit.DAYS))
            datesToSave[i + 1].setCreatedDate(Instant.now().minus(i, ChronoUnit.HOURS))

        }
        when:
        repository.saveAll(datesToSave)
        then:
        def found = statisticService.getSegmentStatistic(workspaceId, 10);
        !found.isEmpty()
    }

    def 'get analysis count result '() {
        given:
        def datesToSave = [] as List<StatisticLog>
        String workspaceId = UUID.randomUUID().toString()
        for (int i = 0; i < 200; i++) {
            datesToSave.add(new StatisticLog().setWorkspaceId(workspaceId).setIntegrationPointKey("a").setAnalysisTime(RandomUtils.nextLong()))
            datesToSave.add(new StatisticLog().setWorkspaceId(workspaceId).setIntegrationPointKey("b").setAnalysisTime(RandomUtils.nextLong()))

        }
        repository.saveAll(datesToSave)
        for (int i = 0; i < 200; i++) {
            datesToSave[i].setCreatedDate(Instant.now().minus(i, ChronoUnit.DAYS))
            datesToSave[i + 1].setCreatedDate(Instant.now().minus(i, ChronoUnit.HOURS))

        }
        when:
        repository.saveAll(datesToSave)
        then:
        def found = statisticService.getAnalysisCount(workspaceId, 1);
        !found.isEmpty()
    }


}
