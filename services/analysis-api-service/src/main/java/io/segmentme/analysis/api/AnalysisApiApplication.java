package io.segmentme.analysis.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication(scanBasePackages = {"io.segmentme"})
public class AnalysisApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnalysisApiApplication.class, args);
    }
}
