package dev.fastball.core.info.action;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author gr@fastball.dev
 * @since 2022/12/17
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface ActionInfo {
    String actionName();

    String actionKey();

    ActionType type();

    boolean closeOnSuccess();
}
