package io.segmentme.core.service.common

import com.fasterxml.jackson.databind.ObjectMapper
import io.segmentme.core.service.configuration.test.ResourceHolder
import io.segmentme.management.service.service.user.UserService
import io.segmentme.web.configuration.AuthUser
import lombok.SneakyThrows
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.mock

@AutoConfigureMockMvc
class BaseControllerTest extends BaseTestWithContext {

    @Autowired
    protected MockMvc mockMvc

    @Autowired
    protected ObjectMapper objectMapper

    @SpringBean
    protected AuthenticationManager authenticationManager = Mock()

    @Autowired
    protected ResourceHolder resourceHolder

    @Autowired
    protected UserService userService;

    @SpringBean(name = "access-control-service")
    protected WebClient accessControlClient = Mock(WebClient.class)

    protected getUriSpecMock = Mock(WebClient.RequestHeadersUriSpec.class)

    protected postUriSpecMock = Mock(WebClient.RequestBodyUriSpec.class)

    protected headersSpecMock = Mock(WebClient.RequestHeadersSpec.class)

    protected requestBodyUriSpec = Mock(WebClient.RequestBodyUriSpec.class)

    protected responseSpecMock = Mock(WebClient.ResponseSpec.class)

    def setup(){
        accessControlClient.get() >> getUriSpecMock
        accessControlClient.post() >> postUriSpecMock

        postUriSpecMock.uri(_) >> requestBodyUriSpec
        requestBodyUriSpec.bodyValue(_) >> headersSpecMock
        headersSpecMock.retrieve() >> responseSpecMock

        getUriSpecMock.uri(_) >> headersSpecMock
        headersSpecMock.retrieve() >> responseSpecMock

        responseSpecMock.toEntity(_) >> Mono.just(ResponseEntity.ok(Boolean.TRUE))
    }

    @SneakyThrows
    protected String serializeToJson(Object o) {
        return objectMapper.writeValueAsString(o)
    }

    protected MockHttpServletRequestBuilder auth(MockHttpServletRequestBuilder builder, String id = uuid(), String email = 'test@test.test') {
        authenticationManager.authenticate(_) >> accessToken(id, email)
        return builder.header("Authorization", "Bearer ${uuid()}")
    }

    private AbstractAuthenticationToken accessToken(String id = uuid(), String email = 'test@test.test') {
        def auth = new TestingAuthenticationToken(new AuthUser().setEmail(email).setId(id).setAcknowledged(true), null)
        auth.setAuthenticated(true)
        return auth
    }

    private String uuid() {
        return UUID.randomUUID().toString()
    }

    protected io.segmentme.analysis.dto.segment.SegmentDto getSegment(String segmentName) {
        return resourceHolder.getSegmentsDto()
            .stream()
            .filter(it -> it.getName().equalsIgnoreCase(segmentName))
            .findFirst()
            .get()
    }

    protected ResultActions sendRequest(MockHttpServletRequestBuilder request, Object body = null) {
        def preparedRequest = auth(request).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
        return body != null ? mockMvc.perform(preparedRequest.content(serializeToJson(body))) : mockMvc.perform(preparedRequest)
    }
}
