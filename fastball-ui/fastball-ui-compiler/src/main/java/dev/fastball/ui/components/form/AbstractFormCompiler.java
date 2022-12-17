package dev.fastball.ui.components.form;

import dev.fastball.core.component.Component;
import dev.fastball.ui.annotation.RecordAction;
import dev.fastball.ui.common.ActionInfo;
import dev.fastball.ui.common.ApiActionInfo;
import dev.fastball.ui.common.ApiActionInfo_AutoValue;
import dev.fastball.ui.components.AbstractComponentCompiler;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author gr@fastball.dev
 * @since 2022/12/14
 */
public abstract class AbstractFormCompiler<T extends Component> extends AbstractComponentCompiler<T, FormProps_AutoValue> {
    private static final String COMPONENT_TYPE = "FastballForm";

    @Override
    protected FormProps_AutoValue compileProps(Class<T> componentClass) {
        FormProps_AutoValue props = new FormProps_AutoValue();
        Type[] genericType = getGenericTypes(componentClass);
        props.componentKey(getComponentKey(componentClass));
        props.fields(buildFieldInfoFromType(genericType[0]));
        List<ActionInfo> recordActions = Arrays.stream(componentClass.getDeclaredMethods())
                .map(this::buildRecordAction).filter(Objects::nonNull).collect(Collectors.toList());
        props.actions(recordActions);
        return props;
    }

    @Override
    protected String getComponentName() {
        return COMPONENT_TYPE;
    }

    protected ActionInfo buildRecordAction(Method method) {
        RecordAction actionAnnotation = method.getDeclaredAnnotation(RecordAction.class);
        if (actionAnnotation == null) {
            return null;
        }
        ApiActionInfo_AutoValue actionInfo = new ApiActionInfo_AutoValue();
        actionInfo.actionKey(method.getName());
        actionInfo.actionName(actionAnnotation.value());
        return actionInfo;
    }

}
