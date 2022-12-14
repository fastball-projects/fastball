package dev.fastball.ui.components.table;


import dev.fastball.ui.components.AbstractComponentCompiler;
import dev.fastball.ui.ActionType;
import dev.fastball.ui.util.TypeCompileUtils;
import dev.fastball.ui.annotation.Sortable;
import dev.fastball.ui.common.TableRecordActionInfo;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
public class TableCompiler extends AbstractComponentCompiler<Table<?, ?>, TableProps> {

    private static final String COMPONENT_TYPE = "FastballTable";

    @Override
    protected TableProps compileProps(Class<Table<?, ?>> componentClass) {
        TableProps props = new TableProps();
        Type[] genericType = getGenericTypes(componentClass);
        props.setColumns(buildTableColumnsFromReturnType(genericType[0]));
        props.setQuery(buildFieldInfoFromType(genericType[1]));
        List<TableRecordActionInfo> recordActions = Arrays.stream(componentClass.getDeclaredMethods()).map(method -> {
            Table.RecordAction actionAnnotation = method.getDeclaredAnnotation(Table.RecordAction.class);
            if (actionAnnotation == null) {
                return null;
            }
            TableRecordActionInfo actionInfo = new TableRecordActionInfo();
            actionInfo.setActionKey(method.getName());
            actionInfo.setActionName(actionAnnotation.value());
            actionInfo.setRefresh(actionAnnotation.refresh());
            actionInfo.setType(ActionType.API);
            return actionInfo;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        props.setRecordActions(recordActions);
        return props;
    }

    @Override
    protected String getComponentName() {
        return COMPONENT_TYPE;
    }

    private List<ColumnInfo> buildTableColumnsFromReturnType(Type returnType) {
        return TypeCompileUtils.compileTypeFields(returnType, ColumnInfo::new, (field, tableColumn) -> {
            Sortable sortable = field.getDeclaredAnnotation(Sortable.class);
            if (sortable != null) {
                tableColumn.setSortable(true);
            }
        });
    }
}
