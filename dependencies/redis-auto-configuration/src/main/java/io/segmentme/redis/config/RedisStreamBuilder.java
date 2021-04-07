package io.segmentme.redis.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamReceiver;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "segmentme.application.redis.stream.enabled", havingValue = "true")
public class RedisStreamBuilder {

    private final RedisStreamProperties redisStreamProperties;

    private final StreamReceiver<String, MapRecord<String, Object, Object>> streamReceiver;

    @SneakyThrows
    public Flux<MapRecord<String, Object, Object>> buildRedisStream() {
        return streamReceiver.receive(
                Consumer.from(redisStreamProperties.getGroupName(), redisStreamProperties.getConsumerName()),
                StreamOffset.create(redisStreamProperties.getEventKey(), ReadOffset.lastConsumed()));
    }
}
