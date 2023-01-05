package dev.fastball.runtime.spring.configure;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.fastball.core.component.runtime.ComponentRegistry;
import dev.fastball.runtime.spring.FastballComponentController;
import dev.fastball.runtime.spring.FastballComponentPostProcessor;
import dev.fastball.runtime.spring.FastballComponentRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author gengrong
 */
@Configuration
public class FastballRuntimeConfiguration implements WebMvcConfigurer {

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    @ConditionalOnMissingBean
    public static FastballComponentRegistryPostProcessor componentRegistryPostProcessor() {
        return new FastballComponentRegistryPostProcessor();
    }


    @Bean
    @ConditionalOnMissingBean
    public ComponentRegistry componentRegistry() {
        return new ComponentRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public FastballComponentController componentController(ComponentRegistry componentRegistry, ObjectMapper objectMapper) {
        return new FastballComponentController(componentRegistry, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public FastballComponentPostProcessor componentPostProcessor(ComponentRegistry componentRegistry) {
        return new FastballComponentPostProcessor(componentRegistry);
    }
}
