package dev.fastball.portal.security.handler;

import dev.fastball.core.Result;
import dev.fastball.portal.security.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class UnloginAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ResponseUtils responseUtils;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        Result<?> result = new Result<>(401, authException.getMessage(), null);
        responseUtils.writeResult(response, result);
    }
}
