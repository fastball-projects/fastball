package dev.fastball.features.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "fastball.storage.minio")
public class MinioConfigProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String defaultBucket;
    private boolean ignoreCertCheck = false;
    private int presignedExpiry = 30;
}
