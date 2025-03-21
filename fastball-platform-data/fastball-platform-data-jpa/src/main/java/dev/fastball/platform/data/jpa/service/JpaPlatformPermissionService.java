package dev.fastball.platform.data.jpa.service;

import dev.fastball.platform.data.jpa.entity.JpaPermissionEntity;
import dev.fastball.platform.data.jpa.repo.PermissionRepo;
import dev.fastball.platform.service.PlatformPermissionService;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;


@RequiredArgsConstructor
public class JpaPlatformPermissionService implements PlatformPermissionService<JpaPermissionEntity> {

    private final PermissionRepo permissionRepo;

    @Override
    public JpaPermissionEntity save(JpaPermissionEntity permission) {
        return permissionRepo.save(permission);
    }

    @Override
    public List<JpaPermissionEntity> save(Collection<JpaPermissionEntity> permissions) {
        return permissionRepo.saveAll(permissions);
    }

    @Override
    public JpaPermissionEntity getPermission(String platform, String permissionType, String permissionCode) {
        return permissionRepo.findByPlatformAndPermissionTypeAndCode(platform, permissionType, permissionCode);
    }

    @Override
    public List<JpaPermissionEntity> getAllPermissions() {
        return permissionRepo.findAll();
    }

    @Override
    public List<JpaPermissionEntity> getPermissionsById(Collection<Long> permissionIds) {
        return permissionRepo.findAllById(permissionIds);
    }

    @Override
    public List<JpaPermissionEntity> getUserPermissions(Long userId) {
        return permissionRepo.findByUserId(userId);
    }

    @Override
    public List<JpaPermissionEntity> getUserPermissions(Long userId, String platform, String permissionType) {
        return permissionRepo.findByUserIdAndPlatformAndType(userId, platform, permissionType);
    }

    @Override
    public JpaPermissionEntity getUserPermission(Long userId, String platform, String permissionType, String permissionCode) {
        return permissionRepo.findByUserIdAndPlatformAndTypeAndCode(userId, platform, permissionType, permissionCode);
    }

}
