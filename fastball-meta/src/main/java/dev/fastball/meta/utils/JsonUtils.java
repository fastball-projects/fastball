package dev.fastball.meta.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.*;

import static dev.fastball.meta.Constants.REF_COMPONENT_GENERATE_CODE_JSON_SERIALIZE_ATTR;

/**
 * @author gr@fastball.dev
 * @since 2022/12/10
 */
public class JsonUtils {
    private JsonUtils() {
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final ObjectMapper COMPONENT_OBJECT_MAPPER = new ObjectMapper();


    static {
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ContextAttributes defAttrs = ContextAttributes.getEmpty().withSharedAttribute(REF_COMPONENT_GENERATE_CODE_JSON_SERIALIZE_ATTR, true);
        COMPONENT_OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setAnnotationIntrospector(new JsonIgnoreOnGenerateCodeAnnotationIntrospector())
                .setDefaultAttributes(defAttrs);
    }

    public static String toJson(Object obj) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(obj);
    }

    public static String toPrettyJson(Object obj) throws JsonProcessingException {
        return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, clazz);
    }

    public static <T> T fromJson(InputStream inputStream, Class<T> clazz) throws IOException {
        return OBJECT_MAPPER.readValue(inputStream, clazz);
    }

    public static <T> T fromJson(InputStream inputStream, Class<T> clazz, ClassLoader classLoader) throws IOException {
        ObjectMapper classLoaderObjectMapper = OBJECT_MAPPER.copy();
        classLoaderObjectMapper.setTypeFactory(TypeFactory.defaultInstance().withClassLoader(classLoader));
        return classLoaderObjectMapper.readValue(inputStream, clazz);
    }

    public static <T> T fromJson(InputStream inputStream, TypeReference<T> valueTypeRef) throws IOException {
        return OBJECT_MAPPER.readValue(inputStream, valueTypeRef);
    }

    public static void writeJson(OutputStream out, Object obj) throws IOException {
        OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(out, obj);
    }

    public static String toComponentJson(Object obj) {
        try {
            return COMPONENT_OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Documented
    @Target({ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface JsonIgnoreOnGenerateCode {

    }

    static class JsonIgnoreOnGenerateCodeAnnotationIntrospector extends JacksonAnnotationIntrospector {
        @Override
        public boolean hasIgnoreMarker(AnnotatedMember m) {
            return super.hasIgnoreMarker(m) || m.hasAnnotation(JsonIgnoreOnGenerateCode.class);
        }
    }
}
