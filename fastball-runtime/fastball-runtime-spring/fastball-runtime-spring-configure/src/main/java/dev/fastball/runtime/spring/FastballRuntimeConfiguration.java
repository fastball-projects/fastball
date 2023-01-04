package dev.fastball.runtime.spring;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.fastball.core.component.runtime.ComponentRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author gengrong
 */
@ComponentScan
@Configuration
public class FastballRuntimeConfiguration implements WebMvcConfigurer {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    public static FastballComponentRegistryPostProcessor componentRegistryPostProcessor() {
        return new FastballComponentRegistryPostProcessor();
    }


    @Bean
    public ComponentRegistry componentRegistry() {
        return new ComponentRegistry();
    }

    @Bean
    public FastballComponentPostProcessor componentPostProcessor(ComponentRegistry componentRegistry) {
        return new FastballComponentPostProcessor(componentRegistry);
    }
}
