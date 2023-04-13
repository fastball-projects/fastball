package dev.fastball.portal.service;

import dev.fastball.portal.dict.PermissionType;
import dev.fastball.portal.entity.JpaUserEntity;
import dev.fastball.portal.exception.UserNotFoundException;
import dev.fastball.portal.model.context.Menu;
import dev.fastball.portal.model.context.Permission;
import dev.fastball.portal.model.context.Role;
import dev.fastball.portal.model.entity.UserEntity;
import dev.fastball.portal.repo.MenuRepo;
import dev.fastball.portal.repo.PermissionRepo;
import dev.fastball.portal.repo.RoleRepo;
import dev.fastball.portal.repo.UserRepo;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JpaFastballPortalService implements FastballPortalService {

    private final UserRepo userRepo;
    private final PermissionRepo permissionRepo;
    private final RoleRepo roleRepo;
    private final MenuRepo menuRepo;

    @Override
    public UserEntity loadAccountByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    public UserEntity loadByUsername(String username) {
        UserEntity account = loadAccountByUsername(username);
        account.setPassword(null);
        return account;
    }

    @Override
    public UserEntity loadByMobile(String mobile) {
        return userRepo.findByMobile(mobile);
    }

    @Override
    public List<Permission> getUserPermission(String userId) {
        return permissionRepo.findByUserId(userId).stream().map(p -> (Permission) p).collect(Collectors.toList());
    }

    @Override
    public List<Permission> getUserPermission(String userId, PermissionType permissionType) {
        return permissionRepo.findByUserIdAndType(userId, permissionType.toString()).stream().map(p -> (Permission) p).collect(Collectors.toList());
    }

    @Override
    public List<Menu> getUserMenu(String userId) {
        List<String> menuIdList = getUserPermission(userId, PermissionType.Menu).stream().map(Permission::getTarget).collect(Collectors.toList());
        return menuRepo.findAllById(menuIdList).stream().map(menu -> (Menu) menu).collect(Collectors.toList());
    }

    @Override
    public List<Role> getUserRole(String userId) {
        JpaUserEntity user = userRepo.findById(userId).orElseThrow(UserNotFoundException::new);
        return user.getRoles().stream().map(role -> (Role) role).collect(Collectors.toList());
    }
}
