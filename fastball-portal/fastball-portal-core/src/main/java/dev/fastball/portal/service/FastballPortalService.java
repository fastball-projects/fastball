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

    List<Permission> getUserPermission(String userId);

    List<Permission> getUserPermission(String userId, PermissionType permissionType);

    List<Menu> getUserMenu(String userId);

    List<Role> getUserRole(String userId);
}
