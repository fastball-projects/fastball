package dev.fastball.platform.core.utils;

import dev.fastball.platform.core.exception.GenerateException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author gr@fastball.dev
 * @since 2023/1/4
 */
public class GeneratorUtils {

    private GeneratorUtils() {
    }

    public static void copyResourceFile(String resourcePath, File targetFile) {
        try (InputStream inputStream = GeneratorUtils.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Copy resource[" + resourcePath + "] not found");
            }
            FileUtils.copyInputStreamToFile(inputStream, targetFile);
        } catch (IOException e) {
            throw new GenerateException(e);
        }
    }
}
