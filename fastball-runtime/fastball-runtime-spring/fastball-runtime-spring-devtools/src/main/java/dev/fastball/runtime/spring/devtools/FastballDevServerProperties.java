package dev.fastball.runtime.spring.devtools;

import dev.fastball.platform.PlatformDevServerConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Setter
@Getter
@ConfigurationProperties(prefix = "fastball")
public class FastballDevServerProperties {
    private Map<String, PlatformDevServerConfig> devServer;
}
