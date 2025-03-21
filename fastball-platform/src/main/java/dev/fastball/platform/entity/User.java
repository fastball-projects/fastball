package dev.fastball.platform.entity;

import dev.fastball.platform.dict.UserStatus;


public interface User extends IdModel {

    String getUsername();

    String getNickname();

    String getMobile();

    String getAvatar();

    UserStatus getStatus();
}
