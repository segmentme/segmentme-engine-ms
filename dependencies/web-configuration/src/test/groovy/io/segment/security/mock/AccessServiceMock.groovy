package io.segment.security.mock

import org.spockframework.spring.SpringBean
import org.springframework.http.ResponseEntity
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import spock.lang.Specification

public class AccessServiceMock extends Specification {

    @SpringBean(name = "access-control-service")
    protected WebClient accessControlClient = Mock(WebClient.class)

    protected getUriSpecMock = Mock(WebClient.RequestHeadersUriSpec.class)

    protected postUriSpecMock = Mock(WebClient.RequestBodyUriSpec.class)

    protected headersSpecMock = Mock(WebClient.RequestHeadersSpec.class)

    protected requestBodyUriSpec = Mock(WebClient.RequestBodyUriSpec.class)

    protected responseSpecMock = Mock(WebClient.ResponseSpec.class)

    def setup() {
        accessControlClient.get() >> getUriSpecMock
        accessControlClient.post() >> postUriSpecMock

        postUriSpecMock.uri(_) >> requestBodyUriSpec
        requestBodyUriSpec.bodyValue(_) >> headersSpecMock
        headersSpecMock.retrieve() >> responseSpecMock

        getUriSpecMock.uri(_) >> headersSpecMock
        headersSpecMock.retrieve() >> responseSpecMock

        responseSpecMock.toEntity(_) >> Mono.just(ResponseEntity.ok(Boolean.TRUE))
    }
}
