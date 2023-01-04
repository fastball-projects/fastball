package dev.fastball.ui.components.table;


import dev.fastball.compile.AbstractComponentCompiler;
import dev.fastball.compile.CompileContext;
import dev.fastball.compile.utils.CompileUtils;
import dev.fastball.core.component.Component;
import dev.fastball.ui.annotation.Action;
import dev.fastball.ui.annotation.RecordAction;
import dev.fastball.ui.common.*;
import dev.fastball.ui.util.TypeCompileUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gr@fastball.dev
 * @since 2022/12/30
 */
public class TableCompiler extends AbstractComponentCompiler<Table<?, ?>, TableProps> {

    private static final String COMPONENT_TYPE = "FastballTable";

    @Override
    protected TableProps compileProps(CompileContext compileContext) {
        TableProps_AutoValue props = new TableProps_AutoValue();
        compileBasicConfig(compileContext, props);
        compileButtons(compileContext, props);
        compileRecordActions(compileContext, props);
        return props;
    }

    @Override
    protected String getComponentName() {
        return COMPONENT_TYPE;
    }

    private List<ColumnInfo> buildTableColumnsFromReturnType(TypeElement returnType, ProcessingEnvironment processingEnv) {
        return TypeCompileUtils.compileTypeFields(returnType, processingEnv, ColumnInfo::new, (field, tableColumn) -> {
            Table.Sortable sortable = field.getAnnotation(Table.Sortable.class);
            if (sortable != null) {
                tableColumn.setSortable(true);
            }
        });
    }

    private void compileRecordActions(CompileContext compileContext, TableProps_AutoValue props) {
        List<ActionInfo> recordActions = CompileUtils
                .getMethods(compileContext.getComponentElement(), compileContext.getProcessingEnv()).values().stream()
                .map(method -> {
                    RecordAction actionAnnotation = method.getAnnotation(RecordAction.class);
                    if (actionAnnotation == null) {
                        return null;
                    }
                    RefreshApiActionInfo_AutoValue actionInfo = new RefreshApiActionInfo_AutoValue();
                    actionInfo.actionKey(method.getSimpleName().toString());
                    actionInfo.actionName(actionAnnotation.value());
                    actionInfo.refresh(true);
                    return actionInfo;
                }).filter(Objects::nonNull).collect(Collectors.toList());
        Table.Config tableConfig = compileContext.getComponentElement().getAnnotation(Table.Config.class);
        if (tableConfig != null) {
            for (Action action : tableConfig.recordActions()) {
                RefreshPopupActionInfo_AutoValue actionInfo = new RefreshPopupActionInfo_AutoValue();
                actionInfo.popupComponent(getReferencedComponentInfo(props, action::component));
                actionInfo.refresh(true);
                actionInfo.popupTitle(action.popupTitle());
                actionInfo.popupType(action.popupType());
                actionInfo.drawerPlacementType(action.drawerPlacementType());
                actionInfo.actionName(action.value());
                recordActions.add(actionInfo);
            }
        }
        props.recordActions(recordActions);
    }

    private void compileButtons(CompileContext compileContext, TableProps_AutoValue props) {
        Table.Config tableConfig = compileContext.getComponentElement().getAnnotation(Table.Config.class);
        if (tableConfig == null) {
            return;
        }
        List<ActionInfo> actionInfoList = Arrays.stream(tableConfig.actions()).map(action -> {
            PopupActionInfo_AutoValue actionInfo = new PopupActionInfo_AutoValue();
            actionInfo.popupTitle(action.popupTitle());
            actionInfo.popupType(action.popupType());
            actionInfo.drawerPlacementType(action.drawerPlacementType());
            actionInfo.popupComponent(getReferencedComponentInfo(props, action::component));
            actionInfo.actionName(action.value());
            return actionInfo;
        }).collect(Collectors.toList());
        props.actions(actionInfoList);
    }

    private void compileBasicConfig(CompileContext compileContext, TableProps_AutoValue props) {
        List<TypeElement> genericTypes = getGenericTypes(compileContext);

        props.columns(buildTableColumnsFromReturnType(genericTypes.get(0), compileContext.getProcessingEnv()));
        props.queryFields(TypeCompileUtils.compileTypeFields(genericTypes.get(1), compileContext.getProcessingEnv()));

        Table.Config tableConfig = compileContext.getComponentElement().getAnnotation(Table.Config.class);
        if (tableConfig == null) {
            return;
        }
        TypeMirror rowExpandedComponent = CompileUtils.getTypeMirrorFromAnnotationValue(tableConfig::rowExpandedComponent);
        if (rowExpandedComponent == null || !Component.class.getCanonicalName().equals(rowExpandedComponent.toString())) {
            TypeElement rowExpandedComponentElement = (TypeElement) compileContext.getProcessingEnv().getTypeUtils().asElement(rowExpandedComponent);
            props.rowExpandedComponent(getReferencedComponentInfo(props, rowExpandedComponentElement));
        }

        if (tableConfig.childrenFieldName().isEmpty()) {
            props.childrenFieldName(tableConfig.childrenFieldName());
        }
    }
}
