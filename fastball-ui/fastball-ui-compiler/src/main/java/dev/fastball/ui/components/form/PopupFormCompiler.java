package dev.fastball.ui.components.form;

import dev.fastball.ui.PopupType;
import dev.fastball.ui.annotation.RecordAction;

import java.lang.reflect.Method;

/**
 * @author gr@fastball.dev
 * @since 2022/12/14
 */
public class PopupFormCompiler extends AbstractFormCompiler<PopupForm<?>> {

    @Override
    protected FormProps_AutoValue compileProps(Class<PopupForm<?>> componentClass) {
        FormProps_AutoValue formProps = super.compileProps(componentClass);
        PopupForm.Config configAnnotation = componentClass.getDeclaredAnnotation(PopupForm.Config.class);
        if (configAnnotation != null) {
            formProps.popupType(configAnnotation.popupType());
        } else {
            formProps.popupType(PopupType.Drawer);
        }
        return formProps;
    }

    @Override
    protected CloseableActionInfo buildRecordAction(Method method) {
        RecordAction actionAnnotation = method.getDeclaredAnnotation(RecordAction.class);
        if (actionAnnotation == null) {
            return null;
        }

        PopupFormApiActionInfo_AutoValue actionInfo = new PopupFormApiActionInfo_AutoValue();
        actionInfo.actionKey(method.getName());
        actionInfo.actionName(actionAnnotation.value());

        PopupForm.CloseOnSuccess closeOnSuccessAnnotation = method.getDeclaredAnnotation(PopupForm.CloseOnSuccess.class);
        if (closeOnSuccessAnnotation != null) {
            actionInfo.closeOnSuccess(true);
        }
        return actionInfo;
    }
}
