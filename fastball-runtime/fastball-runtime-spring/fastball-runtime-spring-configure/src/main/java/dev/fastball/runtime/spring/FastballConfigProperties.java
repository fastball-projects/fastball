package dev.fastball.runtime.spring;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "fastball")
public class FastballConfigProperties {

    private UploadConfig upload;

    @Getter
    @Setter
    public static class UploadConfig {
        private UploadType type;
    }

    public enum UploadType {
        Default, MinIO
    }
}
