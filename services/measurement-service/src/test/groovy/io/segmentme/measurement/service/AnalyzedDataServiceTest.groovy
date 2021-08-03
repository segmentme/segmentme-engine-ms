package io.segmentme.measurement.service

import io.segment.security.mock.AccessServiceMock
import io.segmentme.measurement.service.domain.statistic.AnalyzedData
import io.segmentme.measurement.service.repository.AnalyzedDataRepository
import io.segmentme.measurement.service.service.AnalyzedDataService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AnalyzedDataServiceTest extends AccessServiceMock {

    @Autowired
    private AnalyzedDataService analyzedDataService;


    @Autowired
    private AnalyzedDataRepository analyzedDataRepository

    def 'get latest client analyzed data'() {
        given:
        analyzedDataRepository.save(new AnalyzedData().setHash(UUID.randomUUID().toString()).setIntegrationPointKey("ip1").setClientId("c1").setAnalyzedSegments(["1", "2", "3"]));
        analyzedDataRepository.save(new AnalyzedData().setHash(UUID.randomUUID().toString()).setIntegrationPointKey("ip2").setClientId("c1").setAnalyzedSegments(["1", "2", "3"]));
        analyzedDataRepository.save(new AnalyzedData().setHash(UUID.randomUUID().toString()).setIntegrationPointKey("ip1").setClientId("c2").setAnalyzedSegments(["3", "4", "5"]));
        analyzedDataRepository.save(new AnalyzedData().setHash(UUID.randomUUID().toString()).setIntegrationPointKey("ip1").setClientId("c2").setAnalyzedSegments(["2", "6", "7"]));

        when:
        def found = analyzedDataService.findLatestClientsAnalyzedData("ip1", "2", ["c1", "c2"])

        then:
        found.size() == 2
    }


}
