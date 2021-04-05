package io.segmentme.analysis.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.segmentme.analysis.service.segment.AnalysisService;
import io.segmentme.redis.dto.in.AnalysisMessageIn;
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
public class AnalysisRedisService {

    private final ObjectMapper objectMapper;

    private final AnalysisService analysisService;

    private final ThreadPoolTaskExecutor analysisThreadPool;

    private final RedisStreamBuilder redisStreamBuilder;

    @PostConstruct
    private void init() {
        this.handleRedisMessage();
    }

    private void handleRedisMessage() {
        redisStreamBuilder.buildAnalysisStream()
                .doOnNext(message -> log.info("Received message from redis {} ", message))
                .doOnError(err -> log.error("Redis stream error", err))
                .onErrorResume(t -> Flux.empty())
                .doOnCancel(() -> log.info("Redis stream was cancelled"))
                .doOnTerminate(() -> log.info("Redis stream terminated"))
                .parallel(10)
                .runOn(Schedulers.fromExecutor(analysisThreadPool))
                .map(Record::getValue)
                .map(it -> objectMapper.convertValue(it, AnalysisMessageIn.class))
                .subscribe(it -> analysisService.analyseRedisMessage(it.getBody()), err -> log.error("Analysis error", err));
    }
}
