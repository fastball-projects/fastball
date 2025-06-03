package dev.fastball.core.field;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author gr@fastball.dev
 * @since 2023/1/29
 */
@Getter
@Setter
@JsonSerialize(using = RangeSerialize.class)
@JsonDeserialize(using = RangeDeserialize.class)
public class Range<T extends Comparable<? super T>> {
    private List<T> data;

    public Range() {
        data = new ArrayList<>(2);
    }

    public Range(T start, T end) {
        this.data = Arrays.asList(start, end);
    }

    @JsonCreator
    public Range(List<T> data) {
        this.data = data;
    }

    public T getStart() {
        return data != null && data.size() > 0 ? data.get(0) : null;
    }

    public T getEnd() {
        return data != null && data.size() > 1 ? data.get(1) : null;
    }
}

class RangeSerialize extends JsonSerializer<Range<?>> {

    @Override
    public void serialize(Range<?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeObject(value.getData());
    }
}

class RangeDeserialize extends JsonDeserializer<Range<?>> implements ContextualDeserializer {
    private JavaType contentType;

    public RangeDeserialize() {
    }

    public RangeDeserialize(JavaType contentType) {
        this.contentType = contentType;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        JavaType contextualType = ctxt.getContextualType();
        if (contextualType != null && contextualType.containedTypeCount() == 1) {
            JavaType typeParam = contextualType.containedType(0);
            return new RangeDeserialize(typeParam);
        }
        return this;
    }
    @Override
    public Range<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        if (contentType == null) {
            // fallback
            throw new IllegalStateException("Missing contentType (generic type of Range<T>)");
        }

        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        List<?> list = mapper.readValue(p,
                mapper.getTypeFactory().constructCollectionType(List.class, contentType));

        return new Range(list);
    }

}
