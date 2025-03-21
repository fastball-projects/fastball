package dev.fastball.platform.model;

import dev.fastball.core.annotation.Field;
import dev.fastball.platform.dict.UserStatus;
import lombok.Data;

@Data
public class RegisterUser {

    @Field(title = "手机号")
    private String mobile;

    @Field(title = "用户名")
    private String username;

    @Field(title = "昵称")
    private String nickname;

    @Field(title = "用户密码")
    private String password;

    @Field(title = "用户状态", defaultValue = "Enabled", readonly = true)
    private UserStatus status;
}
