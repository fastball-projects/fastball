package dev.fastball.ui.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;

import static dev.fastball.core.Constants.REF_COMPONENT_GENERATE_CODE_JSON_SERIALIZE_ATTR;

/**
 * @author gr@fastball.dev
 * @since 2022/12/10
 */
public class PrettyJsonUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

    static {
        ContextAttributes defAttrs = ContextAttributes.getEmpty().withSharedAttribute(REF_COMPONENT_GENERATE_CODE_JSON_SERIALIZE_ATTR, true);
        OBJECT_MAPPER.setDefaultAttributes(defAttrs);
    }

    public static String toPrettyJson(Object obj) {
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
