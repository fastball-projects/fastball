package dev.fastball.ui.components.form;

import dev.fastball.ui.ActionType;
import dev.fastball.ui.common.ActionInfo;
import dev.fastball.ui.components.AbstractComponentCompiler;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author gr@fastball.dev
 * @since 2022/12/14
 */
public class FormCompiler extends AbstractComponentCompiler<Form<?>, FormProps> {

    private static final String COMPONENT_TYPE = "FastballForm";

    @Override
    protected FormProps compileProps(Class<Form<?>> componentClass) {
        FormProps props = new FormProps();
        Type[] genericType = getGenericTypes(componentClass);
        props.setComponentKey(getComponentKey(componentClass));
        props.setFields(buildFieldInfoFromType(genericType[0]));
        List<ActionInfo> recordActions = Arrays.stream(componentClass.getDeclaredMethods()).map(method -> {
            Form.RecordAction actionAnnotation = method.getDeclaredAnnotation(Form.RecordAction.class);
            if (actionAnnotation == null) {
                return null;
            }
            ActionInfo actionInfo = new ActionInfo();
            actionInfo.setActionKey(method.getName());
            actionInfo.setActionName(actionAnnotation.value());
            actionInfo.setType(ActionType.API);
            return actionInfo;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        props.setActions(recordActions);
        return props;
    }

    @Override
    protected String getComponentName() {
        return COMPONENT_TYPE;
    }
}
