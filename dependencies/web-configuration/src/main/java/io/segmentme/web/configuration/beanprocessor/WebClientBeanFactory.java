package io.segmentme.web.configuration.beanprocessor;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.util.Optional.ofNullable;


@Slf4j
@Component
@RequiredArgsConstructor
class WebClientBeanFactory {

    WebClient buildWebClientBean(String beanId, WebClientConfigurationProperties configuration) {
        log.info("WebClient {} initialization", beanId);

        var clientHttpConnector = buildConnector(configuration.getConnection())
                .orElseThrow(() -> new UnsatisfiedDependencyException("", beanId, "", "Can't find config for webClient " + beanId));

        return WebClient.builder()
                .baseUrl(configuration.getHost())
                .filter(webClientFilter())
                .clientConnector(clientHttpConnector)
                .build();
    }

    private Optional<ReactorClientHttpConnector> buildConnector(WebClientConfigurationProperties.Connection connection) {
        return ofNullable(connection)
                .map(it -> HttpClient.create()
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, it.getConnectTimeOut())
                        .doOnConnected(connect -> connect
                                .addHandlerLast(new ReadTimeoutHandler(it.getReadTimeOut(), TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(it.getWriteTimeOut(), TimeUnit.MILLISECONDS)))
                ).map(it -> new ReactorClientHttpConnector(it.wiretap(true)));
    }

    private ExchangeFilterFunction webClientFilter() {
        return (request, next) -> {
            ClientRequest.Builder clientRequest = ClientRequest.from(request);
            findHeadersInContext().forEach((key, value) -> clientRequest.headers(header -> header.set(key, value.stream().findFirst().orElse(null))));
            return next.exchange(clientRequest.build());
        };
    }

    private HttpHeaders findHeadersInContext() {
        HttpHeaders headers = new HttpHeaders();

        HttpServletRequest httpServletRequest = ofNullable((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .map(ServletRequestAttributes::getRequest)
                .filter(it -> it.getHeaderNames() != null)
                .orElse(null);

        if (httpServletRequest != null) {
            headers = Collections.list(httpServletRequest.getHeaderNames())
                    .stream()
                    .collect(HttpHeaders::new, (map, value) -> map.put(value, Collections.list(httpServletRequest.getHeaders(value))), HttpHeaders::putAll);
        }

        return headers;
    }
}
