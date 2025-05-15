package dev.fastball.platform.feature.business.context;

import dev.fastball.platform.service.BusinessContextService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static dev.fastball.platform.FastballPlatformConstants.BusinessContext.BUSINESS_CONTEXT_ID_HEADER;
import static dev.fastball.platform.FastballPlatformConstants.BusinessContext.BUSINESS_CONTEXT_KEY_HEADER;

/**
 * @author Geng Rong
 */
@AllArgsConstructor
public class BusinessContextFilter extends OncePerRequestFilter {

    private final BusinessContextService businessContextService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String businessKey = request.getHeader(BUSINESS_CONTEXT_KEY_HEADER);
            String businessId = request.getHeader(BUSINESS_CONTEXT_ID_HEADER);
            if (businessKey != null && businessId != null) {
                BusinessContextAccessor<?> businessContextAccessor = businessContextService.getBusinessContext(businessKey);
                if (businessContextAccessor != null) {
                    BusinessContextItem currentBusinessContext = businessContextAccessor.getBusinessContextById(businessId);
                    if (currentBusinessContext != null) {
                        BusinessContext.set(currentBusinessContext);
                    }
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            BusinessContext.remove();
        }
    }
}
