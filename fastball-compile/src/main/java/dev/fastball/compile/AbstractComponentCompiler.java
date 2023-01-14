package dev.fastball.compile;

import dev.fastball.compile.exception.CompilerException;
import dev.fastball.compile.utils.ElementCompileUtils;
import dev.fastball.core.annotation.Popup;
import dev.fastball.core.annotation.ViewAction;
import dev.fastball.core.annotation.RecordAction;
import dev.fastball.core.component.Component;
import dev.fastball.core.info.action.ActionInfo;
import dev.fastball.core.info.action.ApiActionInfo;
import dev.fastball.core.info.action.PopupActionInfo;
import dev.fastball.core.info.component.ComponentInfo;
import dev.fastball.core.info.component.ComponentInfo_AutoValue;
import dev.fastball.core.info.component.ComponentProps;
import dev.fastball.core.info.component.ReferencedComponentInfo;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gr@fastball.dev
 * @since 2022/12/30
 */
public abstract class AbstractComponentCompiler<T extends Component, P extends ComponentProps> implements ComponentCompiler<P> {

    private final Class<T> basicComponentClass = getBasicComponentClass();

    protected abstract P buildProps(CompileContext compileContext);

    protected abstract String getComponentName();

    protected void compileProps(P props, CompileContext compileContext) {
    }

    @Override
    public ComponentInfo<P> compile(CompileContext compileContext) {
        ComponentInfo_AutoValue<P> componentInfo = new ComponentInfo_AutoValue<>();

        P props = buildProps(compileContext);
        props.componentKey(ElementCompileUtils.getComponentKey(compileContext.getComponentElement()));
        compileProps(props, compileContext);
        componentInfo.props(props);
        componentInfo.material(compileContext.getMaterialRegistry().getMaterial(this.getClass()));
        componentInfo.className(compileContext.getComponentElement().getQualifiedName().toString());
        componentInfo.componentKey(props.componentKey());
        componentInfo.componentPath(ElementCompileUtils.getComponentPath(compileContext.getComponentElement()));
        componentInfo.componentName(getComponentName());
        return componentInfo;
    }

    protected List<TypeElement> getGenericTypes(CompileContext compileContext) {
        DeclaredType declaredType = ElementCompileUtils.getDeclaredInterface(basicComponentClass, compileContext.getComponentElement());
        if (declaredType == null) {
            return Collections.emptyList();
        }
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        return typeArguments.stream().map(type -> (TypeElement) compileContext.getProcessingEnv().getTypeUtils().asElement(type)).collect(Collectors.toList());
    }

    @Override
    public boolean support(CompileContext componentContext) {
        return ElementCompileUtils.isAssignableFrom(basicComponentClass, componentContext);
    }

    protected Class<T> getBasicComponentClass() {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        while (genericSuperclass != null) {
            if (genericSuperclass instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
                if (parameterizedType.getRawType() instanceof Class
                        && ComponentCompiler.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())
                ) {
                    Type componentType = parameterizedType.getActualTypeArguments()[0];
                    if (componentType instanceof Class) {
                        return (Class<T>) componentType;
                    } else if (componentType instanceof ParameterizedType && ((ParameterizedType) componentType).getRawType() instanceof Class) {
                        return (Class<T>) ((ParameterizedType) componentType).getRawType();
                    }
                }
                genericSuperclass = parameterizedType.getRawType();
            } else if (genericSuperclass instanceof Class) {
                genericSuperclass = ((Class<?>) genericSuperclass).getGenericSuperclass();
            }
        }
        throw new CompilerException("can't happened");
    }

    protected ActionInfo buildRecordActionInfo(ExecutableElement method) {
        RecordAction actionAnnotation = method.getAnnotation(RecordAction.class);
        if (actionAnnotation == null) {
            return null;
        }
        return ApiActionInfo.builder()
                .refresh(actionAnnotation.refresh())
                .closePopupOnSuccess(actionAnnotation.closePopupOnSuccess())
                .actionName(actionAnnotation.value())
                .actionKey(method.getSimpleName().toString())
                .build();
    }

    protected ActionInfo buildViewActionInfo(ViewAction viewAction, P props, String actonKey) {
        ActionInfo actionInfo;
        switch (viewAction.type()) {
            case Popup:
                Popup popup = viewAction.popup();
                ReferencedComponentInfo popupComponentInfo = ElementCompileUtils.getReferencedComponentInfo(props, popup::component);
                if (popupComponentInfo == null) {
                    throw new CompilerException("@ViewAction(type=Popup) but @ViewAction.popup.component not config.");
                }
                actionInfo = PopupActionInfo.builder()
                        .popupComponent(popupComponentInfo)
                        .width(popup.width())
                        .popupTitle(popup.popupTitle())
                        .popupType(popup.popupType())
                        .placementType(popup.placementType())
                        .closePopupOnSuccess(popup.closePopupOnSuccess())
                        .refresh(popup.refresh())
                        .build();
                break;
            case Link:
                throw new CompilerException("@ViewAction(type=Link) not supported yet");
            case Menu:
                throw new CompilerException("@ViewAction(type=Menu) not supported yet");
            default:
                throw new CompilerException("@ViewAction(type=" + viewAction.type() + ") not supported yet");
        }
        actionInfo.setActionName(viewAction.value());
        actionInfo.setActionKey(actonKey);
        return actionInfo;
    }
}
