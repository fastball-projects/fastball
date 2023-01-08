package dev.fastball.runtime.spring.devtools;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gr@fastball.dev
 * @since 2022/12/31
 */
@Configuration
public class FastballPortalConfiguration {

    @Bean
    public DevServer devServer() {
        return new DevServer();
    }
}
