package dev.fastball.portal.model.context;

import dev.fastball.portal.dict.UserStatus;

public interface User extends IdModel {

    String getUsername();

    String getNickname();

    String getMobile();

    UserStatus getStatus();
}
