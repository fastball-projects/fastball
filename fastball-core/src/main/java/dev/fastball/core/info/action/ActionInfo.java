package dev.fastball.core.info.action;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

/**
 * @author gr@fastball.dev
 * @since 2022/12/17
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public abstract class ActionInfo {
    private String actionName;

    private String actionKey;

    protected ActionType type;

    private boolean closePopupOnSuccess;

    private boolean refresh;
}
