package dev.fastball.ui.components.table;


import dev.fastball.core.component.Component;
import dev.fastball.core.component.PopupComponent;
import dev.fastball.ui.annotation.Button;
import dev.fastball.ui.annotation.RecordAction;
import dev.fastball.ui.common.ActionInfo;
import dev.fastball.ui.common.PopupActionInfo_AutoValue;
import dev.fastball.ui.components.AbstractComponentCompiler;
import dev.fastball.ui.util.TypeCompileUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
public class TableCompiler2 extends AbstractComponentCompiler<Table<?, ?>, TableProps> {

    private static final String COMPONENT_TYPE = "FastballTable";

    @Override
    protected TableProps compileProps(Class<Table<?, ?>> componentClass) {
        TableProps_AutoValue props = new TableProps_AutoValue();
        Type[] genericType = getGenericTypes(componentClass);
        props.componentKey(getComponentKey(componentClass));
        props.columns(buildTableColumnsFromReturnType(genericType[0]));
        props.queryFields(buildFieldInfoFromType(genericType[1]));
        List<TableRecordActionInfo> recordActions = Arrays.stream(componentClass.getDeclaredMethods()).map(method -> {
            RecordAction actionAnnotation = method.getDeclaredAnnotation(RecordAction.class);
            if (actionAnnotation == null) {
                return null;
            }
            TableRecordApiActionInfo_AutoValue actionInfo = new TableRecordApiActionInfo_AutoValue();
            actionInfo.actionKey(method.getName());
            actionInfo.actionName(actionAnnotation.value());
            actionInfo.refresh(true);
            return actionInfo;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        Table.Config tableConfig = componentClass.getDeclaredAnnotation(Table.Config.class);
        List<ActionInfo> actions = new ArrayList<>();
        if (tableConfig != null) {
            if (tableConfig.rowExpandedComponent() != Component.class) {
                props.rowExpandedComponent(getReferencedComponentInfo(props, tableConfig.rowExpandedComponent()));
            }
            if (tableConfig.childrenFieldName().isEmpty()) {
                props.childrenFieldName(tableConfig.childrenFieldName());
            }
            int buttonIndex = 1;
            for (Button button : tableConfig.buttons()) {
                Class<? extends PopupComponent> popupComponentClass = button.component();
                PopupActionInfo_AutoValue actionInfo = new PopupActionInfo_AutoValue();
                actionInfo.popupComponent(getReferencedComponentInfo(props, popupComponentClass));
                actionInfo.actionName(button.value().isEmpty() ? ("button" + buttonIndex++) : button.value());
                actions.add(actionInfo);
            }
            buttonIndex = 1;
            for (Button button : tableConfig.recordButtons()) {
                Class<? extends PopupComponent> popupComponentClass = button.component();
                TableRecordPopupActionInfo_AutoValue actionInfo = new TableRecordPopupActionInfo_AutoValue();
                actionInfo.popupComponent(getReferencedComponentInfo(props, popupComponentClass));
                actionInfo.refresh(true);
                actionInfo.actionName(button.value().isEmpty() ? ("button" + buttonIndex++) : button.value());
                recordActions.add(actionInfo);
            }
        }
        props.actions(actions);
        props.recordActions(recordActions);
        return props;
    }

    @Override
    public String getComponentName() {
        return COMPONENT_TYPE;
    }

    private List<ColumnInfo> buildTableColumnsFromReturnType(Type returnType) {
        return TypeCompileUtils.compileTypeFields(returnType, ColumnInfo::new, (field, tableColumn) -> {
            Table.Sortable sortable = field.getDeclaredAnnotation(Table.Sortable.class);
            if (sortable != null) {
                tableColumn.setSortable(true);
            }
        });
    }
}
