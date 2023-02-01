package dev.fastball.core.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import dev.fastball.core.component.Range;

import java.io.IOException;

/**
 * @author gr@fastball.dev
 * @since 2022/12/25
 */
public class RangeSerialize extends JsonSerializer<Range<?>> {

    @Override
    public void serialize(Range<?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeObject(value.getData());
    }
}
