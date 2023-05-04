package dev.fastball.runtime.spring.configure;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.fastball.core.component.runtime.ComponentRegistry;
import dev.fastball.core.component.runtime.LookupActionRegistry;
import dev.fastball.core.component.runtime.RecordActionFilterRegistry;
import dev.fastball.core.intergration.storage.ObjectStorageService;
import dev.fastball.features.minio.MinioConfigProperties;
import dev.fastball.features.minio.MinioObjectStorageService;
import dev.fastball.runtime.spring.FastballComponentController;
import dev.fastball.runtime.spring.FastballComponentPostProcessor;
import dev.fastball.runtime.spring.FastballComponentRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author gengrong
 */
@Configuration
public class FastballRuntimeConfiguration implements WebMvcConfigurer {

    private static final String DEFAULT_TIME_FORMATTER = "HH:mm:ss";

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalTime.class, new JsonSerializer<LocalTime>() {
            @Override
            public void serialize(LocalTime localTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                if (Objects.isNull(localTime)) {
                    jsonGenerator.writeString("");
                }
                jsonGenerator.writeString(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMATTER).format(localTime));
            }
        });
        javaTimeModule.addDeserializer(LocalTime.class, new JsonDeserializer<LocalTime>() {
            @Override
            public LocalTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                if (jsonParser.getValueAsString() != null && !jsonParser.getValueAsString().isEmpty()) {
                    return null;
                }
                return LocalTime.parse(jsonParser.getValueAsString(), DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMATTER));
            }
        });
        objectMapper.registerModule(javaTimeModule);
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
    public LookupActionRegistry lookupActionRegistry() {
        return new LookupActionRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public RecordActionFilterRegistry recordActionFilterRegistry() {
        return new RecordActionFilterRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public FastballComponentController componentController(ComponentRegistry componentRegistry, LookupActionRegistry lookupActionRegistry, RecordActionFilterRegistry recordActionFilterRegistry, ObjectMapper objectMapper, ObjectStorageService objectStorageService) {
        return new FastballComponentController(componentRegistry, lookupActionRegistry, recordActionFilterRegistry, objectMapper, objectStorageService);
    }

    @Bean
    @ConditionalOnMissingBean
    public FastballComponentPostProcessor componentPostProcessor(ComponentRegistry componentRegistry, LookupActionRegistry lookupActionRegistry) {
        return new FastballComponentPostProcessor(componentRegistry, lookupActionRegistry);
    }
}
