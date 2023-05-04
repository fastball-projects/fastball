package dev.fastball.features.minio;

import dev.fastball.core.intergration.storage.ObjectStorageService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(MinioConfigProperties.class)
public class MinioStorageConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ObjectStorageService objectStorageService(MinioConfigProperties minioConfigProperties) {
        return new MinioObjectStorageService(minioConfigProperties);
    }

}
