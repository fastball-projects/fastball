package dev.fastball.platform.core.context;

import dev.fastball.platform.core.exception.UnLoginException;
import dev.fastball.platform.core.model.context.Permission;
import dev.fastball.platform.core.model.context.User;
import dev.fastball.platform.core.service.FastballPortalService;
import org.springframework.beans.BeansException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PortalContext implements ApplicationContextAware {

    private static FastballPortalService fastballPlatformService;

    public static Optional<User> currentUserOptional() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return Optional.empty();
        }
        if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
            return Optional.ofNullable(fastballPlatformService.loadByUsername(username));
        }
        return Optional.ofNullable(fastballPlatformService.loadByUsername((String) authentication.getPrincipal()));

    }

    public static User currentUser() {
        return currentUserOptional().orElseThrow(UnLoginException::new);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        fastballPlatformService = applicationContext.getBean(FastballPortalService.class);
    }
}
