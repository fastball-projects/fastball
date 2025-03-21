package dev.fastball.platform.context;

import dev.fastball.platform.exception.UnLoginException;
import dev.fastball.platform.entity.User;
import dev.fastball.platform.service.PlatformUserService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class PortalContext implements ApplicationContextAware {

    private static PlatformUserService fastballPlatformService;

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
        fastballPlatformService = applicationContext.getBean(PlatformUserService.class);
    }
}
