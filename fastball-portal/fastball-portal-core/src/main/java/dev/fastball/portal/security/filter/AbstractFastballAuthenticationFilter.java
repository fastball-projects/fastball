package dev.fastball.portal.security.filter;

import dev.fastball.portal.security.handler.FastballAuthenticationFailureHandler;
import dev.fastball.portal.security.handler.FastballAuthenticationSuccessHandler;
import dev.fastball.portal.security.utils.JwtUtils;
import dev.fastball.portal.security.utils.ResponseUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

public abstract class AbstractFastballAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    protected final ResponseUtils responseUtils;

    protected AbstractFastballAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher, AuthenticationManager authenticationManager, ResponseUtils responseUtils, JwtUtils jwtUtils) {
        super(requiresAuthenticationRequestMatcher, authenticationManager);
        this.setAuthenticationSuccessHandler(new FastballAuthenticationSuccessHandler(responseUtils, jwtUtils));
        this.setAuthenticationFailureHandler(new FastballAuthenticationFailureHandler(responseUtils));
        this.responseUtils = responseUtils;
    }
}
