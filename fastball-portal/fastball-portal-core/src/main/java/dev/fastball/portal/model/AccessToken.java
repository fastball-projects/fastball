package dev.fastball.portal.model;

import lombok.Data;

@Data
public class AccessToken {

    private String token;

    private Long expiration;
}
