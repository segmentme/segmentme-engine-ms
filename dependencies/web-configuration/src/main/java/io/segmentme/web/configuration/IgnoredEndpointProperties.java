package io.segmentme.web.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties("segmentme.application.security.ignored")
public class IgnoredEndpointProperties {

    private List<String> endpoints = List.of();

    private List<String> sdkEndpoints = List.of();

}
