package dev.fastball.platform.core.model.context;

import dev.fastball.platform.core.dict.UserStatus;


public interface User extends IdModel {

    String getUsername();

    String getNickname();

    String getMobile();

    String getAvatar();

    UserStatus getStatus();
}
