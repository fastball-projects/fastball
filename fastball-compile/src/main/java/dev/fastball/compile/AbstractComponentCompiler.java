package dev.fastball.compile;

import dev.fastball.compile.exception.CompilerException;
import dev.fastball.compile.utils.AnnotationClassGetter;
import dev.fastball.compile.utils.ElementCompileUtils;
import dev.fastball.core.annotation.Action;
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
import java.util.HashSet;
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

    protected ActionInfo buildActionInfo(ExecutableElement method) {
        RecordAction actionAnnotation = method.getAnnotation(RecordAction.class);
        if (actionAnnotation == null) {
            return null;
        }
        ApiActionInfo actionInfo = new ApiActionInfo();
        actionInfo.setRefresh(actionAnnotation.refresh());
        actionInfo.setClosePopupOnSuccess(actionAnnotation.closePopupOnSuccess());
        actionInfo.setActionKey(method.getSimpleName().toString());
        actionInfo.setActionName(actionAnnotation.value());
        return actionInfo;
    }

    protected PopupActionInfo buildPopupActionInfo(Action action, P props, String actonKey) {
        PopupActionInfo actionInfo = new PopupActionInfo();
        ReferencedComponentInfo popupComponentInfo = ElementCompileUtils.getReferencedComponentInfo(props, action::component);
        if (popupComponentInfo == null) {
            return null;
        }
        actionInfo.setPopupComponent(popupComponentInfo);
        actionInfo.setWidth(action.width());
        actionInfo.setPopupTitle(action.popupTitle());
        actionInfo.setPopupType(action.popupType());
        actionInfo.setPlacementType(action.placementType());
        actionInfo.setActionName(action.value());
        actionInfo.setActionKey(actonKey);
        actionInfo.setRefresh(action.refresh());
        actionInfo.setClosePopupOnSuccess(action.closePopupOnSuccess());
        return actionInfo;
    }
}
