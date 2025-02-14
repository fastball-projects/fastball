package dev.fastball.platform.core.service;

import dev.fastball.platform.core.model.RegisterUser;
import dev.fastball.platform.core.model.context.Role;
import dev.fastball.platform.core.model.context.User;
import dev.fastball.platform.core.model.entity.UserEntity;

public interface FastballPortalService {

    User registerUser(RegisterUser user);

    Role registerRole(String roleCode, String roleName, String description);

    void changePassword(Long userId, String password, String newPassword);

    boolean resetPasswordByUserId(Long userId, String password);

    boolean resetPasswordByUserName(String username, String password);

    boolean resetPasswordByMobile(String mobile, String password);

    UserEntity loadAccountByUsername(String username);

    User loadByUsername(String username);

    User loadByMobile(String mobile);
}
