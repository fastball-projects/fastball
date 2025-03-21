package dev.fastball.platform.security.filter;

import dev.fastball.platform.model.LoginByPassword;
import dev.fastball.platform.security.handler.FastballAuthenticationFailureHandler;
import dev.fastball.platform.security.handler.FastballAuthenticationSuccessHandler;
import dev.fastball.platform.security.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;


public class PasswordAuthenticationFilter extends AbstractFastballAuthenticationFilter {
    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/api/login", "POST");

    public PasswordAuthenticationFilter(AuthenticationManager authenticationManager, ResponseUtils responseUtils, FastballAuthenticationSuccessHandler fastballAuthenticationSuccessHandler, FastballAuthenticationFailureHandler fastballAuthenticationFailureHandler) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager, responseUtils, fastballAuthenticationSuccessHandler, fastballAuthenticationFailureHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        LoginByPassword login = responseUtils.read(request.getInputStream(), LoginByPassword.class);
        if (login == null) {
            return null;
        }
        return getAuthenticationManager().authenticate(convertToken(login));
    }

    private UsernamePasswordAuthenticationToken convertToken(LoginByPassword login) {
        return new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword());
    }
}
