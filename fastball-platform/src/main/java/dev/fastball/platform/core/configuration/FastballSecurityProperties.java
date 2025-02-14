package dev.fastball.platform.core.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "fastball.security")
public class FastballSecurityProperties {

    private String[] anonymousPath;

    private FastballJwtProperties jwt;

    public String[] getAnonymousPath() {
        return anonymousPath != null ? anonymousPath : new String[0];
    }
}
