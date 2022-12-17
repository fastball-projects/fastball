package dev.fastball.ui.components.table;

import dev.fastball.auto.value.annotation.AutoValue;
import dev.fastball.ui.common.ActionInfo;
import dev.fastball.ui.common.ComponentProps;
import dev.fastball.ui.common.FieldInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
@AutoValue
public interface TableProps extends ComponentProps {

    String headerTitle();

    List<ColumnInfo> columns();

    List<FieldInfo> query();

    List<ActionInfo> actions();

    List<TableRecordActionInfo> recordActions();
}
