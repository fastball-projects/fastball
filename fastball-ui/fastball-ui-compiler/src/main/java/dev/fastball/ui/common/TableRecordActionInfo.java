package dev.fastball.ui.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TableRecordActionInfo extends ActionInfo {
    private boolean refresh;
}
