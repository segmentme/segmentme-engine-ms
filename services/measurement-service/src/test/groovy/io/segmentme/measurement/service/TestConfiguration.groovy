package io.segmentme.measurement.service

import io.segmentme.redis.config.RedisStreamBuilder
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import reactor.core.publisher.Flux
import spock.lang.Specification

@org.springframework.boot.test.context.TestConfiguration
@ActiveProfiles("test")
public class TestConfiguration extends Specification {


    @Bean
    public RedisStreamBuilder streamReceiver() {
        def mock = Mock(RedisStreamBuilder.class)
        mock.buildRedisStream() >> Flux.just()
        return mock
    }
}
