package dev.fastball.runtime.spring.devtools;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author gr@fastball.dev
 * @since 2022/12/31
 */
@AutoConfiguration
public class FastballPortalConfiguration {

    @Bean
    public DevServer devServer() {
        return new DevServer();
    }
}
