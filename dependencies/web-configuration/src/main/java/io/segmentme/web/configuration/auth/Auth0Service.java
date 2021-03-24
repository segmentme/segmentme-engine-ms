package io.segmentme.web.configuration.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class Auth0Service {

    private static final String USER_URL = "/users/";

    private static final String VERIFICATION_EMAIL_URL = "/jobs/verification-email";

    @Qualifier("auth0-client")
    private final WebClient auth0WebClient;

    public void acknowledge(String userId) {
        log.info("Acknowledge request");

        auth0WebClient.patch()
            .uri(USER_URL + userId)
            .bodyValue(AppMetadata.of(Map.of("acknowledged", true)))
            .retrieve().toEntity(String.class)
            .doOnSuccess(response -> log.info("Acknowledge response status {}", response.getStatusCode()))
            .subscribe();
    }

    public void resendVerificationEmail(String userId) {
        log.info("Resend verification email request");

        auth0WebClient.post().uri(VERIFICATION_EMAIL_URL)
            .bodyValue(Map.of("user_id", userId))
            .retrieve().toEntity(String.class)
            .doOnSuccess(response -> log.info("Resend verification email response status: {}", response.getStatusCode()))
            .flatMap(response -> !response.getStatusCode().is2xxSuccessful() ? Mono.error(new RuntimeException("Error sending email")) : Mono.just(Objects.requireNonNull(response.getBody())))
            .doOnSuccess(response -> log.info("Resend verification email result {}", response))
            .block();
    }


    @Data
    @AllArgsConstructor(staticName = "of")
    public static class AppMetadata {

        @JsonProperty("app_metadata")
        public Map<String, Object> metadata;
    }
}
