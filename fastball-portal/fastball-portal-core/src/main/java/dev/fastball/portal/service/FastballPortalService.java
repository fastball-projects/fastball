package dev.fastball.portal.service;

import dev.fastball.portal.dict.PermissionType;
import dev.fastball.portal.model.context.Menu;
import dev.fastball.portal.model.context.Permission;
import dev.fastball.portal.model.context.Role;
import dev.fastball.portal.model.context.User;
import dev.fastball.portal.model.entity.UserEntity;

import java.util.List;

public interface FastballPortalService {

    UserEntity loadAccountByUsername(String username);

    User loadByUsername(String username);

    User loadByMobile(String mobile);

    List<Permission> getUserPermission(Long userId);

    List<Permission> getUserPermission(Long userId, PermissionType permissionType);

    List<Menu> getUserMenu(Long userId);

    List<Role> getUserRole(Long userId);
}
