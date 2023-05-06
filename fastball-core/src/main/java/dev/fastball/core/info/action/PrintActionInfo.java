package dev.fastball.core.info.action;

import dev.fastball.core.info.basic.RefComponentInfo;
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
public class PrintActionInfo extends ActionInfo {
    protected final ActionType type = ActionType.Print;

    private RefComponentInfo printComponent;
}