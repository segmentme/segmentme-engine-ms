package io.segmentme.web.configuration.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class Auth0SecurityConfig {

    private final static String WELL_KNOWN_URL = "%s.well-known/jwks.json";

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private final String issuer;

    @Value("${auth0.audience}")
    private final String audience;

    private final ObjectMapper objectMapper;

    @Bean
    public JwtDecoder jwtDecoder() throws MalformedURLException {
        var wellKnownUrl = String.format(WELL_KNOWN_URL, issuer);
        var jwkSource = new RemoteJWKSet<>(new URL(wellKnownUrl), new DefaultResourceRetriever(1000, 1000));
        var signatureAlgorithms = getSignatureAlgorithms(jwkSource);

        var jwtDecoder = NimbusJwtDecoder.withJwkSetUri(wellKnownUrl)
                .jwsAlgorithms(algs -> algs.addAll(signatureAlgorithms))
                .build();

        jwtDecoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));

        var audienceValidator = new AudienceValidator(audience);
        var withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        var withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

        jwtDecoder.setJwtValidator(withAudience);
        return jwtDecoder;
    }


    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider(JwtDecoder jwtDecoder) {
        var jwtAuthenticationProvider = new JwtAuthenticationProvider(jwtDecoder);
        jwtAuthenticationProvider.setJwtAuthenticationConverter(new JwtTokenConverter(objectMapper));
        return jwtAuthenticationProvider;
    }


    private Set<SignatureAlgorithm> getSignatureAlgorithms(JWKSource<SecurityContext> jwkSource) {
        try {
            return jwkSource.get(new JWKSelector(getJWKMatcher()), null)
                    .stream()
                    .map(this::getAlgorithm)
                    .flatMap(Collection::stream)
                    .map(it -> SignatureAlgorithm.from(it.getName()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        } catch (KeySourceException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private Collection<JWSAlgorithm> getAlgorithm(JWK jwk) {
        if (jwk.getAlgorithm() != null) {
            return Collections.singleton(JWSAlgorithm.parse(jwk.getAlgorithm().getName()));
        }
        if (jwk.getKeyType() == KeyType.RSA) {
            return JWSAlgorithm.Family.RSA;
        }
        if (jwk.getKeyType() == KeyType.EC) {
            return JWSAlgorithm.Family.EC;
        }
        return Collections.emptySet();
    }

    private JWKMatcher getJWKMatcher() {
        return new JWKMatcher.Builder()
                .publicOnly(true)
                .keyUses(KeyUse.SIGNATURE, null)
                .keyTypes(KeyType.RSA, KeyType.EC)
                .build();
    }
}