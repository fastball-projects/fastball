package dev.fastball.runtime.spring.devtools;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

/**
 * @author gr@fastball.dev
 * @since 2022/12/31
 */
@Configuration
@EnableConfigurationProperties({FastballDevServerProperties.class})
public class FastballDevToolsConfiguration {

    @Bean
    public DevServer devServer(@Nullable FastballDevServerProperties properties) {
        return new DevServer(properties);
    }
}
