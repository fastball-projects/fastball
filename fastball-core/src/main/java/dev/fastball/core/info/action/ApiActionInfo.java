package dev.fastball.core.info.action;

import dev.fastball.auto.value.annotation.AutoValue;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
@AutoValue
public interface ApiActionInfo extends ActionInfo {
    ActionType type = ActionType.API;
}