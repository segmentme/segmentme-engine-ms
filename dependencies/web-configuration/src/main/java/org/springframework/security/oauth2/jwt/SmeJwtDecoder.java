package org.springframework.security.oauth2.jwt;

import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmeJwtDecoder {
    public static <T extends JwtDecoder> T
    fromIssuerLocation(String issuer) throws MalformedURLException {
        Assert.hasText(issuer, "issuer cannot be empty");
        Map<String, Object> configuration = JwtDecoderProviderConfigurationUtils
            .getConfigurationForIssuerLocation(issuer);
        return (T) withProviderConfiguration(configuration, issuer);
    }

    /**
     * Validate provided issuer and build {@link JwtDecoder} from <a href=
     * "https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfigurationResponse">OpenID
     * Provider Configuration Response</a> and
     * <a href="https://tools.ietf.org/html/rfc8414#section-3.2">Authorization Server
     * Metadata Response</a>.
     * @param configuration the configuration values
     * @param issuer the <a href=
     * "https://openid.net/specs/openid-connect-core-1_0.html#IssuerIdentifier">Issuer</a>
     * @return {@link JwtDecoder}
     */
    private static JwtDecoder withProviderConfiguration(Map<String, Object> configuration, String issuer) throws MalformedURLException {
        JwtDecoderProviderConfigurationUtils.validateIssuer(configuration, issuer);
        OAuth2TokenValidator<Jwt> jwtValidator = JwtValidators.createDefaultWithIssuer(issuer);
        String jwkSetUri = configuration.get("jwks_uri").toString();
        RemoteJWKSet<SecurityContext> jwkSource = new RemoteJWKSet<>(new URL(jwkSetUri),new DefaultResourceRetriever(1000,1000));
        Set<SignatureAlgorithm> signatureAlgorithms = JwtDecoderProviderConfigurationUtils
            .getSignatureAlgorithms(jwkSource);
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri)
            .jwsAlgorithms((algs) -> algs.addAll(signatureAlgorithms)).build();
        jwtDecoder.setJwtValidator(jwtValidator);
        return jwtDecoder;
    }
}
