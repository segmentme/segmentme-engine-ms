package io.segmentme.measurement.service;

import io.segmentme.redis.config.RedisProperties;
import lombok.SneakyThrows;
import org.springframework.test.context.ActiveProfiles;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@org.springframework.boot.test.context.TestConfiguration
@ActiveProfiles("test")
public class TestConfiguration {

    private RedisServer redisServer;

    @SneakyThrows
    public TestConfiguration(RedisProperties redisProperties) {
        this.redisServer = new RedisServer(redisProperties.getPort());
    }

    @PostConstruct
    public void postConstruct() {
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() {
        redisServer.stop();
    }
}
