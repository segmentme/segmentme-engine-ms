package io.segmentme.web.configuration.auth;

import io.segmentme.security.IntegrationPointKeySecurityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Slf4j
public class SdkSecurityFilter extends GenericFilterBean {

    private static final String INTEGRATION_POINT_KEY = "integration-point-key";

    private final List<? extends RequestMatcher> requestMatchers;

    private final IntegrationPointKeySecurityService integrationPointKeySecurityService;


    public SdkSecurityFilter(List<? extends RequestMatcher> requestMatchers, IntegrationPointKeySecurityService integrationPointKeySecurityService) {
        this.requestMatchers = requestMatchers;
        this.integrationPointKeySecurityService = integrationPointKeySecurityService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (requestMatchers.stream().anyMatch(it -> it.matches(httpRequest))) {
            String integrationPointKey = httpRequest.getHeader(INTEGRATION_POINT_KEY);
            log.info("Request with integration-point-key in header {}", integrationPointKey);

            if (StringUtils.isBlank(integrationPointKey)) {
                throw new AccessDeniedException("Integration Point Key not presented");
            }
            if (!integrationPointKeySecurityService.isExist(integrationPointKey)) {
                throw new AccessDeniedException("Invalid credentials");
            }
        }

        chain.doFilter(httpRequest, response);
    }
}
