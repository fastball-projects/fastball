package dev.fastball.portal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

import static dev.fastball.core.Constants.REF_COMPONENT_GENERATE_CODE_JSON_SERIALIZE_ATTR;

/**
 * @author gr@fastball.dev
 * @since 2022/12/25
 */
public class ComponentSerialize extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            gen.writeRawValue("<" + value + " />");
        }
    }
}
