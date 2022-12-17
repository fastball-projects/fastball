package dev.fastball.ui.common;

import dev.fastball.auto.value.annotation.AutoValue;
import dev.fastball.ui.ActionType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
@AutoValue
public interface ApiActionInfo extends ActionInfo {
    ActionType type = ActionType.API;
}