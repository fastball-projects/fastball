package dev.fastball.features.minio;

import dev.fastball.core.intergration.storage.AbstractObjectStorageService;
import dev.fastball.core.intergration.storage.exception.GeneratePresignedUrlException;
import dev.fastball.core.intergration.storage.exception.GetObjectException;
import dev.fastball.core.intergration.storage.exception.UploadObjectException;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@Slf4j
public class MinioObjectStorageService extends AbstractObjectStorageService {

    private final MinioConfigProperties configProperties;

    private MinioClient client;

    public MinioObjectStorageService(MinioConfigProperties configProperties) {
        this.configProperties = configProperties;
        try {
            this.client = MinioClient.builder()
                    .endpoint(configProperties.getEndpoint())
                    .credentials(configProperties.getAccessKey(), configProperties.getSecretKey())
                    .build();
            if (configProperties.isIgnoreCertCheck()) {
                this.client.ignoreCertCheck();
            }
        } catch (Exception e) {
            this.client = null;
            log.warn("MinIO init failed", e);
        }
    }

    @Override
    public File getFile(String bucket, String objectName) {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .build();
        try {
            client.getObject(getObjectArgs);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new GetObjectException(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void upload(InputStream inputStream, String bucket, String objectName) {
        try {
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(inputStream, inputStream.available(), -1)
                    .build();
            client.putObject(args);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new UploadObjectException(e.getMessage(), e);
        }
    }

    @Override
    public String generateUploadUrl(String bucket, String objectName) {
        return generatePresignedUrl(bucket, objectName, Method.PUT);
    }

    @Override
    public String generateReadUrl(String bucket, String objectName) {
        return generatePresignedUrl(bucket, objectName, Method.GET);
    }

    @Override
    public String getDefaultBucket() {
        return configProperties.getDefaultBucket();
    }

    private String generatePresignedUrl(String bucket, String objectName, Method method) {
        GetPresignedObjectUrlArgs preSignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                .method(method)
                .bucket(bucket)
                .expiry(configProperties.getPresignedExpiry())
                .object(objectName).build();
        try {
            return client.getPresignedObjectUrl(preSignedObjectUrlArgs);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidResponseException |
                 InvalidKeyException | IOException | NoSuchAlgorithmException | XmlParserException |
                 ServerException e) {
            throw new GeneratePresignedUrlException(e.getMessage(), e);
        }
    }
}
