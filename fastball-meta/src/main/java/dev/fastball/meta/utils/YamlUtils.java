package dev.fastball.meta.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author gr@fastball.dev
 * @since 2022/12/10
 */
public class YamlUtils {

    private YamlUtils() {
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(new YAMLFactory())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static <T> T fromYaml(InputStream inputStream, Class<T> clazz) throws IOException {
        return OBJECT_MAPPER.readValue(inputStream, clazz);
    }

    public static <T> T fromYaml(InputStream inputStream, TypeReference<T> valueTypeRef) throws IOException {
        return OBJECT_MAPPER.readValue(inputStream, valueTypeRef);
    }
}
