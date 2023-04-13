package dev.fastball.portal.model.entity;

import dev.fastball.portal.dict.UserStatus;
import dev.fastball.portal.model.context.User;

public interface UserEntity extends User, BasicEntity {

    String getPassword();

    void setPassword(String password);

    void setUsername(String username);

    void setNickname(String nickname);

    void setMobile(String mobile);

    void setStatus(UserStatus status);
}
