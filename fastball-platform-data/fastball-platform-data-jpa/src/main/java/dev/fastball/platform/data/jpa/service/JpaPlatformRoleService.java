package dev.fastball.platform.data.jpa.service;

import dev.fastball.platform.data.jpa.entity.JpaRoleEntity;
import dev.fastball.platform.data.jpa.entity.JpaUserEntity;
import dev.fastball.platform.data.jpa.repo.PermissionRepo;
import dev.fastball.platform.data.jpa.repo.RoleRepo;
import dev.fastball.platform.data.jpa.repo.UserRepo;
import dev.fastball.platform.exception.RoleNotFoundException;
import dev.fastball.platform.exception.UserNotFoundException;
import dev.fastball.platform.service.PlatformRoleService;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Date;
import java.util.List;


@RequiredArgsConstructor
public class JpaPlatformRoleService implements PlatformRoleService<JpaRoleEntity> {

    private final RoleRepo roleRepo;
    private final UserRepo userRepo;
    private final PermissionRepo permissionRepo;

    @Override
    public JpaRoleEntity save(JpaRoleEntity role) {
        return roleRepo.save(role);
    }

    @Override
    public Collection<JpaRoleEntity> save(Collection<JpaRoleEntity> roles) {
        return List.of();
    }

    @Override
    public void saveUserRoles(Long userId, Collection<Long> roleIds) {
        JpaUserEntity user = userRepo.findById(userId).orElseThrow(UserNotFoundException::new);
        user.setRoles(roleRepo.findAllById(roleIds));
        user.setNickname(user.getNickname());
        user.setLastUpdatedAt(new Date());
        userRepo.save(user);
    }

    @Override
    public void saveRolePermissions(Long roleId, Collection<Long> permissionIds) {
        JpaRoleEntity role = roleRepo.findById(roleId).orElseThrow(RoleNotFoundException::new);
        role.setPermissions(permissionRepo.findAllById(permissionIds));
        roleRepo.save(role);
    }

    @Override
    public List<JpaRoleEntity> getUserRoles(Long userId) {
        JpaUserEntity user = userRepo.findById(userId).orElseThrow(UserNotFoundException::new);
        return user.getRoles();
    }

    @Override
    public List<JpaRoleEntity> getAllRole() {
        return roleRepo.findAll();
    }

    @Override
    public JpaRoleEntity loadRoleByCode(String roleCode) {
        return roleRepo.findByCode(roleCode);
    }
}
