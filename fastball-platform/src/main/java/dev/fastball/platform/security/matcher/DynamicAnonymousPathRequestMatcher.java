package dev.fastball.platform.security.matcher;

import dev.fastball.core.component.runtime.ComponentRegistry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.AntPathMatcher;

@RequiredArgsConstructor
public class DynamicAnonymousPathRequestMatcher implements RequestMatcher {
    private static final String COMPONENT_PATH = "/api/fastball/component/{componentKey}/**";
    private final ComponentRegistry componentRegistry;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public boolean matches(HttpServletRequest request) {
        String requestPath = request.getServletPath();
        if (pathMatcher.match(COMPONENT_PATH, requestPath)) {
            String componentKey = extractComponentKey(requestPath);
            if (componentRegistry.getAnonymousAccessComponentBeans().contains(componentKey)) {
                return true;
            }
        }
        return false;
    }

    private String extractComponentKey(String requestPath) {
        return pathMatcher.extractUriTemplateVariables(COMPONENT_PATH, requestPath).get("componentKey");
    }
}
