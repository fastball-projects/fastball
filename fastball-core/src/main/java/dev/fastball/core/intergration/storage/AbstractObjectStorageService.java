package dev.fastball.core.intergration.storage;

import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.UUID;

public abstract class AbstractObjectStorageService implements ObjectStorageService {

    @Override
    public String generateObjectName() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String generateObjectName(String prefix) {
        String objectName = generateObjectName();
        if (!StringUtils.hasText(prefix)) {
            return objectName;
        }
        if (prefix.endsWith("/")) {
            return prefix + objectName;
        }
        return prefix + "/" + objectName;
    }
}
