package dev.fastball.meta.action;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonDeserialize
public class ApiActionInfo extends ActionInfo {
    protected final ActionType type = ActionType.API;
    private boolean uploadFileAction;
    private boolean downloadFileAction;
    private String confirmMessage;
}