package dev.fastball.core.utils;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import dev.fastball.core.component.Range;

import java.io.IOException;

/**
 * @author gr@fastball.dev
 * @since 2022/12/25
 */
public class RangeDeserialize extends JsonDeserializer<Range<?>> {

    @Override
    public Range<?> deserialize(JsonParser p, DeserializationContext context) throws IOException, JacksonException {
        return null;
    }
}
