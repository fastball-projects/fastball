package dev.fastball.ui.table;

import dev.fastball.ui.common.FieldInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ColumnInfo extends FieldInfo {
    private boolean sortable = false;
    private boolean copyable = false;
}
