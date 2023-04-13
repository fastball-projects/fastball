package dev.fastball.portal.security.filter;

import dev.fastball.portal.model.LoginByPassword;
import dev.fastball.portal.security.utils.JwtUtils;
import dev.fastball.portal.security.utils.ResponseUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class PasswordAuthenticationFilter extends AbstractFastballAuthenticationFilter {
    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/api/login", "POST");

    public PasswordAuthenticationFilter(AuthenticationManager authenticationManager, ResponseUtils responseUtils, JwtUtils jwtUtils) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager, responseUtils, jwtUtils);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
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
