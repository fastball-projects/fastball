package dev.fastball.ui.components.form;

import dev.fastball.compile.AbstractComponentCompiler;
import dev.fastball.compile.CompileContext;
import dev.fastball.compile.utils.CompileUtils;
import dev.fastball.ui.annotation.RecordAction;
import dev.fastball.ui.common.ActionInfo;
import dev.fastball.ui.common.ApiActionInfo_AutoValue;
import dev.fastball.ui.util.TypeCompileUtils;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author gr@fastball.dev
 * @since 2022/12/14
 */
public class FormCompiler extends AbstractComponentCompiler<Form<?>, FormProps_AutoValue> {
    private static final String COMPONENT_TYPE = "FastballForm";

    @Override
    protected FormProps_AutoValue compileProps(CompileContext compileContext) {
        FormProps_AutoValue props = new FormProps_AutoValue();
        List<TypeElement> genericTypes = getGenericTypes(compileContext);
        props.fields(TypeCompileUtils.compileTypeFields(genericTypes.get(0), compileContext.getProcessingEnv()));
        compileRecordActions(compileContext, props);
        Form.Config config = compileContext.getComponentElement().getAnnotation(Form.Config.class);
        if (config != null) {
            props.showReset(config.showReset());
        } else {
            props.showReset(true);
        }
        return props;
    }

    @Override
    public String getComponentName() {
        return COMPONENT_TYPE;
    }

    protected void compileRecordActions(CompileContext compileContext, FormProps_AutoValue props) {
        List<ActionInfo> actionInfoList = CompileUtils.getMethods(compileContext.getComponentElement(), compileContext.getProcessingEnv())
                .values().stream().map(this::buildActionInfo).filter(Objects::nonNull).collect(Collectors.toList());
        props.actions(actionInfoList);
    }

    protected ActionInfo buildActionInfo(ExecutableElement method) {
        RecordAction actionAnnotation = method.getAnnotation(RecordAction.class);
        if (actionAnnotation == null) {
            return null;
        }
        ApiActionInfo_AutoValue actionInfo = new ApiActionInfo_AutoValue();
        actionInfo.actionKey(method.getSimpleName().toString());
        actionInfo.actionName(actionAnnotation.value());
        return actionInfo;
    }
}
