package io.segmentme.measurement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication(scanBasePackages = {"io.segmentme.core","io.segmentme"})
@EnableRetry
public class MeasurementApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeasurementApplication.class, args);
    }
}
