package dev.fastball.meta.action;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dev.fastball.meta.utils.ActionInfoDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author gr@fastball.dev
 * @since 2022/12/17
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = ActionInfoDeserializer.class)
public abstract class ActionInfo implements Comparable<ActionInfo> {
    private String actionName;

    private String actionKey;
    private String componentKey;

    protected ActionType type;

    private boolean closePopupOnSuccess;

    private boolean refresh;

    private int order;

    @Override
    public int compareTo(ActionInfo o) {
        return Integer.compare(this.order, o.order);
    }
}
