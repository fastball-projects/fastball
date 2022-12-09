package dev.fastball.ui.common;

import dev.fastball.ui.ActionType;
import lombok.Data;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
@Data
public class ActionInfo {
    private String actionName;

    private String actionKey;

    private ActionType type;
}
