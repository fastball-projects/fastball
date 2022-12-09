package dev.fastball.ui.table;


import dev.fastball.core.annotation.UIApi;
import dev.fastball.core.component.AbstractComponentCompiler;
import dev.fastball.ui.ActionType;
import dev.fastball.ui.TypeCompileUtils;
import dev.fastball.ui.annotation.Sortable;
import dev.fastball.ui.common.FieldInfo;
import dev.fastball.ui.common.TableRecordActionInfo;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
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
        Type superInterface = Arrays.stream(componentClass.getGenericInterfaces())
                .filter(i -> ((ParameterizedType) i).getRawType() == Table.class)
                .findFirst().orElseThrow(() -> new RuntimeException("never happened"));
        if (!(superInterface instanceof ParameterizedType)) {
            throw new RuntimeException("never happened");
        }
        Type[] genericType = ((ParameterizedType) superInterface).getActualTypeArguments();
        props.setColumns(buildTableColumnsFromReturnType(genericType[0]));
        props.setQuery(buildSearchFieldsFromQueryParam(genericType[1]));
        Map<String, Method> actionMethodMap = new HashMap<>();
        Arrays.stream(Table.class.getDeclaredMethods())
                .filter(method -> method.getDeclaredAnnotation(UIApi.class) != null)
                .forEach(method -> {
                    Method declaredMethod = Arrays.stream(componentClass.getDeclaredMethods())
                            .filter(m -> !m.isBridge() && m.getName().equals(method.getName()))
                            .findFirst().orElseThrow(() -> new RuntimeException("never happened"));
                    Type returnWrapperType = declaredMethod.getGenericReturnType();
                    actionMethodMap.put(declaredMethod.getName(), declaredMethod);
                });
        List<TableRecordActionInfo> recordActions = Arrays.stream(componentClass.getDeclaredMethods())
                .map(method -> {
                    Table.RecordButton buttonAnnotation = method.getDeclaredAnnotation(Table.RecordButton.class);
                    if (buttonAnnotation == null) {
                        return null;
                    }
                    TableRecordActionInfo actionInfo = new TableRecordActionInfo();
                    actionInfo.setActionKey(method.getName());
                    actionInfo.setActionName(buttonAnnotation.text());
                    actionInfo.setRefresh(buttonAnnotation.refresh());
                    actionInfo.setType(ActionType.API);
                    actionMethodMap.put(actionInfo.getActionKey(), method);
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

    private List<FieldInfo> buildSearchFieldsFromQueryParam(Type queryType) {
        return TypeCompileUtils.compileTypeFields(queryType, FieldInfo::new);
    }
}
