package io.segmentme.web.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.segmentme.core.dto.error.ErrorType;
import io.segmentme.core.dto.error.SimpleErrorDto;
import io.segmentme.security.IntegrationPointKeySecurityService;
import io.segmentme.web.configuration.auth.SdkSecurityFilter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.codec.CharEncoding;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final IgnoredEndpointProperties ignoredEndpointProperties;

    private static final List<RequestMatcher> IGNORED_PATH_MATCHER = List.of(
            new AntPathRequestMatcher("/analysis/connect"),
            new AntPathRequestMatcher("/analysis/analyze/**")
    );

    private final ObjectMapper objectMapper;

    private final AuthenticationManager authenticationManager;

    @Lazy
    private final IntegrationPointKeySecurityService integrationPointKeySecurityService;

    @Bean
    public FilterRegistrationBean<CorsFilter> filterRegistrationBean() {
        var source = new UrlBasedCorsConfigurationSource();
        var config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        var bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        var ignoredEndpoints = ignoredEndpointProperties.getEndpoints().stream().map(AntPathRequestMatcher::new).collect(Collectors.toList());
        var sdkEndpoints = ignoredEndpointProperties.getSdkEndpoints().stream().map(AntPathRequestMatcher::new).collect(Collectors.toList());

        http.cors().disable()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/actuator/**")
                .permitAll()
                .requestMatchers(request -> isNotEmpty(ignoredEndpoints) && ignoredEndpoints.stream().noneMatch(it -> it.matches(request))).authenticated();

        if (isNotEmpty(sdkEndpoints)) {
            http.addFilterBefore(new SdkSecurityFilter(sdkEndpoints, integrationPointKeySecurityService), BasicAuthenticationFilter.class);
        }

        http.exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
                .authenticationEntryPoint(entryPointExceptionHandler())
                .and()
                .oauth2ResourceServer()
                .accessDeniedHandler(accessDeniedHandler())
                .authenticationEntryPoint(entryPointExceptionHandler())
                .jwt()
                .authenticationManager(authenticationManager);
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return (request, response, exception) -> writeException(response, HttpStatus.FORBIDDEN, exception);
    }

    private AuthenticationEntryPoint entryPointExceptionHandler() {
        return (request, response, exception) -> writeException(response, HttpStatus.UNAUTHORIZED, exception);
    }

    @SneakyThrows
    private void writeException(HttpServletResponse response, HttpStatus status, RuntimeException exception) {
        var out = response.getOutputStream();
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(CharEncoding.UTF_8);
        objectMapper.writeValue(out, new SimpleErrorDto(ErrorType.AUTHENTICATION_ERROR, exception.getMessage()));
        out.flush();
    }
}
