package io.segmentme.analysis

import io.segmentme.analysis.helper.AnalysisResourceHolder
import io.segmentme.analysis.redis.AnalysisRedisService
import io.segmentme.analysis.redis.RedisStreamBuilder
import io.segmentme.core.SpringCoreDataApplication
import io.segmentme.redis.config.MessagePublisher
import lombok.extern.slf4j.Slf4j
import org.spockframework.spring.SpringBean
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import static org.mockito.Mockito.mock

@Slf4j
@SpringBootTest(classes = SpringCoreDataApplication.class)
@ActiveProfiles("test")
@Import([AnalysisResourceHolder.class])
@ComponentScan("io.segmentme")
abstract class BaseTestWithContext extends Specification {

    @SpringBean
    protected AnalysisRedisService analysisRedisService = mock(AnalysisRedisService.class)

    @SpringBean
    protected RedisStreamBuilder redisStreamBuilder = mock(RedisStreamBuilder.class)

    @SpringBean
    protected MessagePublisher messagePublisher = mock(MessagePublisher.class)
}
