package dev.fastball.platform.security.service;

import dev.fastball.platform.dict.UserStatus;
import dev.fastball.platform.exception.UserDisabledException;
import dev.fastball.platform.entity.UserWithPassword;
import dev.fastball.platform.service.PlatformUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;

@RequiredArgsConstructor
public class DefaultUserDetailsService implements UserDetailsService {

    private final PlatformUserService portalService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserWithPassword user = portalService.loadAccountByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        if (user.getStatus() == UserStatus.Disabled) {
            throw new UserDisabledException();
        }
        return User.withUsername(username).password(user.getPassword()).authorities(new ArrayList<>()).build();
    }
}
