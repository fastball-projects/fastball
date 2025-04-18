package dev.fastball.platform.feature.business.context;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author Geng Rong
 */
public class BusinessContextSerializer extends JsonSerializer<BusinessContextItem> {
    @Override
    public void serialize(BusinessContextItem value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("id", value.id());
        gen.writeStringField("title", value.title());
        gen.writeEndObject();
    }
}
