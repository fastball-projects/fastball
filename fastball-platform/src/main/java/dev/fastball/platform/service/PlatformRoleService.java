package dev.fastball.platform.service;

import dev.fastball.platform.entity.Role;

import java.util.Collection;
import java.util.List;

public interface PlatformRoleService<T extends Role> {

    T save(T role);

    Collection<T> save(Collection<T> roles);

    void saveUserRoles(Long userId, Collection<Long> roleIds);

    void saveRolePermissions(Long roleId, Collection<Long> permissionIds);

    T loadRoleByCode(String roleCode);

    List<T> getUserRoles(Long userId);

    List<T> getAllRole();
}
