package dev.fastball.platform.service;

import dev.fastball.platform.entity.Permission;

import java.util.Collection;
import java.util.List;

public interface PlatformPermissionService<T extends Permission> {
    T save(T permission);

    List<T> save(Collection<T> permissions);

    T getPermission(String platform, String permissionType, String permissionCode);

    T getUserPermission(Long userId, String platform, String permissionType, String permissionCode);

    List<T> getAllPermissions();

    List<T> getPermissionsById(Collection<Long> permissionIds);

    List<T> getUserPermissions(Long userId);

    List<T> getUserPermissions(Long userId, String platform, String permissionType);
}
