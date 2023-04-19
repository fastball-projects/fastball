package dev.fastball.portal.config;

import dev.fastball.portal.entity.JpaUserEntity;
import dev.fastball.portal.repo.MenuRepo;
import dev.fastball.portal.repo.PermissionRepo;
import dev.fastball.portal.repo.RoleRepo;
import dev.fastball.portal.repo.UserRepo;
import dev.fastball.portal.service.FastballPortalService;
import dev.fastball.portal.service.JpaFastballPortalService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@AutoConfiguration
@EntityScan(basePackageClasses = JpaUserEntity.class)
@EnableJpaRepositories(basePackageClasses = UserRepo.class)
public class JpaFastballSecurityConfiguration {
    @Bean
    public FastballPortalService fastballPortalService(UserRepo fastballUserRepo, PermissionRepo permissionRepo, RoleRepo roleRepo, MenuRepo menuRepo) {
        return new JpaFastballPortalService(fastballUserRepo, permissionRepo, roleRepo, menuRepo);
    }
}
