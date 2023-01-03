package dev.fastball.core.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author gr@fastball.dev
 * @since 2022/12/10
 */
public class YamlUtils {

    private YamlUtils() {
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(new YAMLFactory());

    public static <T> T fromYaml(InputStream inputStream, Class<T> clazz) throws IOException {
        return OBJECT_MAPPER.readValue(inputStream, clazz);
    }

    public static <T> T fromYaml(InputStream inputStream, TypeReference<T> valueTypeRef) throws IOException {
        return OBJECT_MAPPER.readValue(inputStream, valueTypeRef);
    }
}
