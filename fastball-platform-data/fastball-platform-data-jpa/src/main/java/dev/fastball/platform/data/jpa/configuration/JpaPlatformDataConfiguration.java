package dev.fastball.platform.data.jpa.configuration;

import dev.fastball.platform.data.jpa.entity.JpaUserEntity;
import dev.fastball.platform.data.jpa.repo.PermissionRepo;
import dev.fastball.platform.data.jpa.repo.RoleRepo;
import dev.fastball.platform.data.jpa.repo.ThirdPartyUserRepo;
import dev.fastball.platform.data.jpa.repo.UserRepo;
import dev.fastball.platform.data.jpa.service.JpaPlatformPermissionService;
import dev.fastball.platform.data.jpa.service.JpaPlatformRoleService;
import dev.fastball.platform.data.jpa.service.JpaPlatformUserService;
import dev.fastball.platform.service.PlatformPermissionService;
import dev.fastball.platform.service.PlatformRoleService;
import dev.fastball.platform.service.PlatformUserService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

@AutoConfiguration
@EntityScan(basePackageClasses = JpaUserEntity.class)
@EnableJpaRepositories(basePackageClasses = UserRepo.class)
@ComponentScan(basePackages = "dev.fastball.platform.data.jpa")
public class JpaPlatformDataConfiguration {

    @Bean
    public PlatformUserService platformUserService(UserRepo userRepo, ThirdPartyUserRepo thirdPartyUserRepo, PasswordEncoder passwordEncoder) {
        return new JpaPlatformUserService(userRepo, thirdPartyUserRepo, passwordEncoder);
    }

    @Bean
    public PlatformRoleService<?> platformRoleService(RoleRepo roleRepo, UserRepo userRepo, PermissionRepo permissionRepo) {
        return new JpaPlatformRoleService(roleRepo, userRepo, permissionRepo);
    }

    @Bean
    public PlatformPermissionService<?> platformPermissionService(PermissionRepo permissionRepo) {
        return new JpaPlatformPermissionService(permissionRepo);
    }
}
