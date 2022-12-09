package dev.fastball.ui.table;

import dev.fastball.ui.common.FieldInfo;
import dev.fastball.ui.common.TableRecordActionInfo;
import lombok.Data;

import java.util.List;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
@Data
public class TableProps {

    String headerTitle;

    List<ColumnInfo> columns;

    List<FieldInfo> query;

    List<TableRecordActionInfo> recordActions;
}
