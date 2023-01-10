package dev.fastball.core.info.action;

import lombok.Data;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
@Data
public class ApiActionInfo extends ActionInfo {
    protected ActionType type = ActionType.API;
}