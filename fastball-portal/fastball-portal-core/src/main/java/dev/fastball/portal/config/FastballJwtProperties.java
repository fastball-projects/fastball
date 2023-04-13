package dev.fastball.portal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "fastball.security.jwt")
public class FastballJwtProperties {

    private String secret = "fastball_jwt_default_secret_key_1_fastball_jwt_default_secret_key_2";
    private String tokenPrefix = "Bearer ";
    private String tokenHeader = "Authorization";
    private int expiration = 60 * 60;
    private int refreshTime = 5 * 60;
}
