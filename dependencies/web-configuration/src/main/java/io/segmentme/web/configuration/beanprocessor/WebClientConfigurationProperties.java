package io.segmentme.web.configuration.beanprocessor;

import lombok.Data;

@Data
public class WebClientConfigurationProperties {

    private String host;

    private String serviceName;

    private Connection connection;

    @Data
    public static class Connection {

        private int connectTimeOut = 10_000;

        private int readTimeOut = 10_000;

        private int writeTimeOut = 10_000;
    }
}