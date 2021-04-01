package io.segmentme.measurement

import io.segment.security.mock.AccessServiceMock
import io.segmentme.analysis.domain.statistic.StatisticLog
import io.segmentme.measurement.repository.StatisticRepository
import io.segmentme.measurement.service.StatisticService
import org.apache.commons.lang3.RandomUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

import java.time.Instant
import java.time.temporal.ChronoUnit

@SpringBootTest
class StatisticServiceTest extends AccessServiceMock {

    @Autowired
    private StatisticRepository repository
    @Autowired
    private StatisticService statisticService;

    def 'get segment count result '() {
        given:
        def datesToSave = [] as List<StatisticLog>
        String workspaceId = UUID.randomUUID().toString()
        for (int i = 0; i < 200; i++) {
            datesToSave.add(new StatisticLog().setWorkspaceId(workspaceId).setSegmentStatistics(Arrays.asList(new StatisticLog.SegmentStatistic().setSegmentId("a").setResult(true))).setAnalysisTime(RandomUtils.nextLong()))
            datesToSave.add(new StatisticLog().setWorkspaceId(workspaceId).setSegmentStatistics(Arrays.asList(new StatisticLog.SegmentStatistic().setSegmentId("b").setResult(true))).setAnalysisTime(RandomUtils.nextLong()))
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
