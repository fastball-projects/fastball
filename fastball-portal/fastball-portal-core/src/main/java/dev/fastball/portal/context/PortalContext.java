package dev.fastball.portal.context;

import dev.fastball.portal.model.context.Permission;
import dev.fastball.portal.model.context.User;
import dev.fastball.portal.exception.UnLoginException;
import dev.fastball.portal.service.FastballPortalService;
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

    private static FastballPortalService fastballPortalService;

    public static Optional<User> currentUserOptional() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }
        String username = (String) authentication.getPrincipal();
        return Optional.of(fastballPortalService.loadByUsername(username));

    }

    public static User currentUser() {
        return currentUserOptional().orElseThrow(UnLoginException::new);
    }

    public static boolean hasPermission(String permissionCode) {
        return userPermissionCodeSet(currentUser().getId()).contains(permissionCode);
    }

    @Cacheable("current_user_permission_set")
    private static Set<String> userPermissionCodeSet(String userId) {
        return fastballPortalService.getUserPermission(userId).stream()
                .map(Permission::getCode)
                .collect(Collectors.toSet());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        fastballPortalService = applicationContext.getBean(FastballPortalService.class);
    }
}
