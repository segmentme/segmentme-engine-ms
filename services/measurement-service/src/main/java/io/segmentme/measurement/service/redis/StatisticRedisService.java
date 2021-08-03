package io.segmentme.measurement.service.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.segmentme.measurement.service.dto.analysis.CollectedAnalysysStatisticDto;
import io.segmentme.measurement.service.service.StatisticManager;
import io.segmentme.redis.config.RedisStreamBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.Record;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticRedisService {

    private final ObjectMapper objectMapper;

    private final ThreadPoolTaskExecutor analysisThreadPool;

    private final RedisStreamBuilder redisStreamBuilder;

    private final StatisticManager statisticManager;

    @PostConstruct
    private void init() {
        this.handleRedisMessage();
    }

    private void handleRedisMessage() {
        redisStreamBuilder.buildRedisStream()
                .doOnNext(message -> log.info("Received message from redis {} ", message))
                .doOnError(err -> log.error("Redis stream error", err))
                .onErrorResume(t -> Flux.empty())
                .doOnCancel(() -> log.info("Redis stream was cancelled"))
                .doOnTerminate(() -> log.info("Redis stream terminated"))
                .parallel(10)
                .runOn(Schedulers.fromExecutor(analysisThreadPool))
                .map(Record::getValue)
                .map(it -> objectMapper.convertValue(it, CollectedAnalysysStatisticDto.class))
                .subscribe(statisticManager::saveStatistic, err -> log.error("Statistic redis message error", err));
    }
}
