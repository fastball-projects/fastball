package dev.fastball.ui.common;

import dev.fastball.ui.ActionType;

/**
 * @author gr@fastball.dev
 * @since 2022/12/17
 */
public interface ActionInfo {
    String actionName();

    String actionKey();

    ActionType type();
}
