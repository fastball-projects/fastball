package dev.fastball.meta.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import dev.fastball.meta.component.ComponentInfo;
import dev.fastball.meta.component.ComponentInfo_AutoValue;
import dev.fastball.meta.component.ComponentProps;
import dev.fastball.meta.material.UIMaterial;

import java.io.IOException;

public class ComponentInfoDeserializer extends StdDeserializer<ComponentInfo<? extends ComponentProps>> {

    static final String COMPONENT_KEY = "componentKey";
    static final String COMPONENT_NAME = "componentName";
    static final String COMPONENT_PATH = "componentPath";
    static final String CLASS_NAME = "className";
    static final String MATERIAL = "material";
    static final String CUSTOMIZED = "customized";
    static final String PROPS = "props";

    protected ComponentInfoDeserializer() {
        super(ComponentInfo.class);
    }

    @Override
    public ComponentInfo<?> deserialize(JsonParser p, DeserializationContext context) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        JsonNode componentKey = node.get(COMPONENT_KEY);
        JsonNode componentName = node.get(COMPONENT_NAME);
        JsonNode componentPath = node.get(COMPONENT_PATH);
        JsonNode className = node.get(CLASS_NAME);
        JsonNode customized = node.get(CUSTOMIZED);
        JsonNode materialJson = node.get(MATERIAL);
        JsonNode props = node.get(PROPS);
        ComponentInfo_AutoValue.ComponentInfo_AutoValueBuilder<? super ComponentProps> builder = ComponentInfo_AutoValue.builder();
        if (componentKey != null) {
            builder.componentKey(componentKey.asText());
        }
        if (componentName != null) {
            String name = componentName.asText();
            builder.componentName(name);
            if (props != null) {
                Class<? extends ComponentProps> propsClass = ComponentPropsTypeRegistry.get(name);
                builder.props(p.getCodec().treeToValue(props, propsClass));
            }
        }
        if (componentPath != null) {
            builder.componentPath(componentPath.asText());
        }
        if (className != null) {
            builder.className(className.asText());
        }
        if (customized != null) {
            builder.customized(customized.asBoolean());
        }
        if (materialJson != null) {
            UIMaterial material = p.getCodec().treeToValue(materialJson, UIMaterial.class);
            builder.material(material);
        }
        return builder.build();
    }
}
