package dev.fastball.core.info.action;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public abstract class ActionInfo {
    private String actionName;

    private String actionKey;
    private String componentKey;

    protected ActionType type;

    private boolean closePopupOnSuccess;

    private boolean refresh;
}
