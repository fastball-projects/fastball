package dev.fastball.platform.service;

import dev.fastball.platform.dict.UserStatus;
import dev.fastball.platform.entity.ThirdPartyUser;
import dev.fastball.platform.entity.User;
import dev.fastball.platform.entity.UserWithPassword;
import dev.fastball.platform.exception.UserNotFoundException;
import dev.fastball.platform.model.RegisterUser;

public interface PlatformUserService {

    User registerUser(RegisterUser user);

    void changePassword(Long userId, String password, String newPassword);

    boolean resetPasswordByUserId(Long userId, String password);

    boolean resetPasswordByUserName(String username, String password);

    boolean resetPasswordByMobile(String mobile, String password);

    UserWithPassword loadByUsernameWithPassword(String username);

    User loadByUserId(Long userId);

    User loadByUsername(String username);

    User loadByMobile(String mobile);

    ThirdPartyUser loadByThirdParty(String thirdPartyPlatform, String outId);

    ThirdPartyUser bindThirdPartyUser(Long userId, String thirdPartyPlatform, String outId);

    UserStatus getUserStatus(Long userId) throws UserNotFoundException;

    boolean enableUser(Long userId);

    boolean disableUser(Long userId);
}
