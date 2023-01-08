package dev.fastball.compile;

import dev.fastball.compile.exception.CompilerException;
import dev.fastball.compile.utils.AnnotationClassGetter;
import dev.fastball.compile.utils.ElementCompileUtils;
import dev.fastball.core.annotation.UIComponent;
import dev.fastball.core.component.Component;
import dev.fastball.core.info.component.*;

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

    protected abstract P compileProps(CompileContext compileContext);

    protected abstract String getComponentName();

    @Override
    public ComponentInfo<P> compile(CompileContext compileContext) {
        ComponentInfo_AutoValue<P> componentInfo = new ComponentInfo_AutoValue<>();

        P props = compileProps(compileContext);
        componentInfo.props(props);
        if (props.componentKey() == null || props.componentKey().trim().isEmpty()) {
            props.componentKey(getComponentKey(compileContext.getComponentElement()));
        }

        componentInfo.material(compileContext.getMaterialRegistry().getMaterial(this.getClass()));
        componentInfo.className(compileContext.getComponentElement().getQualifiedName().toString());
        componentInfo.componentKey(props.componentKey());
        componentInfo.componentName(getComponentName());
        return componentInfo;
    }

    protected String getComponentKey(TypeElement componentElement) {
        UIComponent frontendComponentAnnotation = componentElement.getAnnotation(UIComponent.class);
        if (frontendComponentAnnotation.value().isEmpty()) {
            return componentElement.getSimpleName().toString();
        }
        return frontendComponentAnnotation.value();
    }

    protected List<TypeElement> getGenericTypes(CompileContext compileContext) {
        DeclaredType declaredType = ElementCompileUtils.getDeclaredInterface(basicComponentClass, compileContext.getComponentElement());
        if (declaredType == null) {
            return Collections.emptyList();
        }
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        return typeArguments.stream().map(type -> (TypeElement) compileContext.getProcessingEnv().getTypeUtils().asElement(type)).collect(Collectors.toList());
    }

    protected ReferencedComponentInfo getReferencedComponentInfo(P props, AnnotationClassGetter annotationClassGetter) {
        TypeMirror popupComponentClass = ElementCompileUtils.getTypeMirrorFromAnnotationValue(annotationClassGetter);
        if (popupComponentClass == null) {
            throw new RuntimeException("can't happened");
        }
        TypeElement componentTypeElement = (TypeElement) ((DeclaredType) popupComponentClass).asElement();
        return getReferencedComponentInfo(props, componentTypeElement);
    }

    protected ReferencedComponentInfo getReferencedComponentInfo(P props, TypeElement componentTypeElement) {
        if (props.referencedComponentInfoList() == null) {
            props.referencedComponentInfoList(new HashSet<>());
        }
        ReferencedComponentInfo_AutoValue refComponentInfo = new ReferencedComponentInfo_AutoValue();
        String path = componentTypeElement.getQualifiedName().toString().replace("\\.", "/");
        refComponentInfo.component("Component___" + (props.referencedComponentInfoList().size() + 1));
        refComponentInfo.componentClass(componentTypeElement.getQualifiedName().toString());
        refComponentInfo.componentPackage("@");
        refComponentInfo.componentPath(path);
        refComponentInfo.componentName(componentTypeElement.getSimpleName().toString());
        props.referencedComponentInfoList().add(refComponentInfo);
        return refComponentInfo;
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
}
