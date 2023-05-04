package dev.fastball.core.intergration.storage;

import java.io.File;
import java.io.InputStream;

public interface ObjectStorageService {

    File getFile(String bucket, String objectName);

    void upload(InputStream inputStream, String bucket, String objectName);

    String generateUploadUrl(String bucket, String objectName);

    String generateReadUrl(String bucket, String objectName);

    String generateObjectName();

    String generateObjectName(String prefix);

    String getDefaultBucket();

    default File getFile(String objectName) {
        return getFile(getDefaultBucket(), objectName);
    }

    default String upload(InputStream inputStream) {
        String objectName = generateObjectName();
        upload(inputStream, getDefaultBucket(), objectName);
        return objectName;
    }

    default void upload(InputStream inputStream, String objectName) {
        upload(inputStream, getDefaultBucket(), objectName);
    }

    default String generateUploadUrl() {
        return generateUploadUrl(getDefaultBucket(), generateObjectName());
    }

    default String generateUploadUrl(String objectName) {
        return generateUploadUrl(getDefaultBucket(), objectName);
    }

    default String generateReadUrl(String objectName) {
        return generateReadUrl(getDefaultBucket(), objectName);
    }
}
