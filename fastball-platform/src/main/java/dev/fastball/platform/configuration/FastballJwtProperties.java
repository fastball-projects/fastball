package dev.fastball.platform.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
public class FastballJwtProperties {
    private String secret = "fastball_jwt_default_secret_key_1_fastball_jwt_default_secret_key_2";
    private String tokenPrefix = "Bearer ";
    private String tokenHeader = "Authorization";
    private int expiration = 60 * 60;
    private int refreshTime = 5 * 60;
}
