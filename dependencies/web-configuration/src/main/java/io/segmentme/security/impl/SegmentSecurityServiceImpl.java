package io.segmentme.security.impl;

import io.segmentme.security.SegmentSecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Slf4j
@Service(value = "segmentSecurityService")
@RequiredArgsConstructor
public class SegmentSecurityServiceImpl implements SegmentSecurityService {

    @Qualifier("access-control-service")
    private final WebClient accessControlClient;

    @Override
    public boolean isManaged(String segmentId, String userId) {
        return Optional.ofNullable(accessControlClient.get()
                .uri("/segment/has-access?segmentId=" + segmentId)
                .retrieve()
                .toEntity(Boolean.class)
                .block())
                .map(HttpEntity::getBody)
                .map(Boolean.TRUE::equals)
                .orElse(false);
    }
}