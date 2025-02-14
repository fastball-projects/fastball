package dev.fastball.platform.core.model.entity;

import dev.fastball.platform.core.dict.UserStatus;
import dev.fastball.platform.core.model.context.User;

public interface UserEntity extends User {

    String getPassword();

    void setPassword(String password);

    void setUsername(String username);

    void setNickname(String nickname);

    void setMobile(String mobile);

    void setAvatar(String avatar);

    void setStatus(UserStatus status);
}
