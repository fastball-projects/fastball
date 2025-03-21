package dev.fastball.platform.security.filter;

import dev.fastball.platform.security.handler.FastballAuthenticationFailureHandler;
import dev.fastball.platform.security.handler.FastballAuthenticationSuccessHandler;
import dev.fastball.platform.security.utils.ResponseUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

public abstract class AbstractFastballAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    protected final ResponseUtils responseUtils;

    protected AbstractFastballAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher, AuthenticationManager authenticationManager, ResponseUtils responseUtils, FastballAuthenticationSuccessHandler fastballAuthenticationSuccessHandler, FastballAuthenticationFailureHandler fastballAuthenticationFailureHandler) {
        super(requiresAuthenticationRequestMatcher, authenticationManager);
        this.setAuthenticationSuccessHandler(fastballAuthenticationSuccessHandler);
        this.setAuthenticationFailureHandler(fastballAuthenticationFailureHandler);
        this.responseUtils = responseUtils;
    }
}
