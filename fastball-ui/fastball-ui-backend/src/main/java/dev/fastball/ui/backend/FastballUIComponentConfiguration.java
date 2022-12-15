package dev.fastball.ui.backend;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.fastball.core.component.ComponentRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author gengrong
 */
@ComponentScan
@Configuration
public class FastballUIComponentConfiguration implements WebMvcConfigurer {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    public ComponentRegistry componentRegistry() {
        return new ComponentRegistry();
    }

    @Bean
    public ComponentPostProcessor componentPostProcessor(ComponentRegistry componentRegistry) {
        return new ComponentPostProcessor(componentRegistry);
    }
}
