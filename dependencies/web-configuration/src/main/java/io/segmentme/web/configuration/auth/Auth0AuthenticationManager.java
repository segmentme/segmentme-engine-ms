package io.segmentme.web.configuration.auth;

import io.segmentme.AuthAcknowledger;
import io.segmentme.web.configuration.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class Auth0AuthenticationManager implements AuthenticationManager {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    private final Auth0Service auth0Service;

    private final Optional<AuthAcknowledger> userFacade;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var authenticate = jwtAuthenticationProvider.authenticate(authentication);

        AuthUser authUser = (AuthUser) authenticate.getPrincipal();
        if (userFacade.isPresent() && authenticate.isAuthenticated() && (!Boolean.TRUE.equals(authUser.isAcknowledged()) || authUser.getId() == null)) {

            String userId = userFacade.map(it -> it.acknowledgeUser(authUser.getExternalId(), authUser.getEmail(), authUser.getFullName()))
                    .orElse(null);

            auth0Service.acknowledge(authUser.getExternalId(), userId);
        }

        return authenticate;
    }
}