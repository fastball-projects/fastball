package dev.fastball.meta.action;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dev.fastball.meta.basic.RefComponentInfo;
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
public class PrintActionInfo extends ActionInfo {
    protected final ActionType type = ActionType.Print;

    private RefComponentInfo printComponent;
}