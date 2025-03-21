package dev.fastball.platform.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordModel {
    private String password;
    private String newPassword;
}
