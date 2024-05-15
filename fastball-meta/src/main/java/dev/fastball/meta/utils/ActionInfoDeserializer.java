package dev.fastball.meta.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import dev.fastball.meta.action.*;

import java.io.IOException;

public class ActionInfoDeserializer extends StdDeserializer<ActionInfo> {

    static final String ACTION_TYPE = "type";

    protected ActionInfoDeserializer() {
        super(ActionInfo.class);
    }

    @Override
    public ActionInfo deserialize(JsonParser p, DeserializationContext context) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        ActionType actionType = ActionType.valueOf(node.get(ACTION_TYPE).asText());
        Class<? extends ActionInfo> actionInfoClass;
        if (actionType == ActionType.Popup) {
            actionInfoClass = PopupActionInfo.class;
        } else if (actionType == ActionType.API) {
            actionInfoClass = ApiActionInfo.class;
        } else if (actionType == ActionType.Print) {
            actionInfoClass = PrintActionInfo.class;
        } else {
            throw new IllegalArgumentException("Unsupported action type: " + actionType);
        }
        return p.getCodec().treeToValue(node, actionInfoClass);
    }
}