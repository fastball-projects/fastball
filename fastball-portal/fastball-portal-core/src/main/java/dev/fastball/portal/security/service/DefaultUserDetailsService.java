package dev.fastball.portal.security.service;

import dev.fastball.portal.model.entity.UserEntity;
import dev.fastball.portal.service.FastballPortalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;

@RequiredArgsConstructor
public class DefaultUserDetailsService implements UserDetailsService {

    private final FastballPortalService portalService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = portalService.loadAccountByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return User.withUsername(username).password(user.getPassword()).authorities(new ArrayList<>()).build();
    }
}
