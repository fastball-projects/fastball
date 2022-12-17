package dev.fastball.ui.components;

import dev.fastball.core.annotation.UIApi;
import dev.fastball.core.annotation.UIComponent;
import dev.fastball.core.component.Component;
import dev.fastball.core.component.ComponentBean;
import dev.fastball.core.component.ComponentCompiler;
import dev.fastball.core.info.ComponentInfo;
import dev.fastball.ui.common.ReferencedComponentInfo;
import dev.fastball.ui.common.ReferencedComponentInfo_AutoValue;
import dev.fastball.ui.util.TypeCompileUtils;
import dev.fastball.ui.common.FieldInfo;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
public abstract class AbstractComponentCompiler<T extends Component, P> implements ComponentCompiler<T, P> {

    private final Class<?> basicComponentClass = getBasicComponentClass();

    protected abstract P compileProps(Class<T> componentClass);

    protected abstract String getComponentName();

    @Override
    public ComponentInfo<P> compile(Class<T> componentClass) {
        ComponentInfo<P> componentInfo = new ComponentInfo<>();
        componentInfo.setComponentKey(getComponentKey(componentClass));
        componentInfo.setComponentName(getComponentName());
        P props = compileProps(componentClass);
        componentInfo.setProps(props);
        return componentInfo;
    }

    @Override
    public ComponentBean buildComponentBean(Component component) {
        ComponentBean componentBean = new ComponentBean();
        componentBean.setComponent(component);
        componentBean.setMethodMap(getApiMethodMapper(component.getClass()));
        return componentBean;
    }

    @Override
    public String getComponentKey(Class<? extends Component> componentClass) {
        UIComponent frontendComponentAnnotation = componentClass.getAnnotation(UIComponent.class);
        if (frontendComponentAnnotation.value().isEmpty()) {
            return componentClass.getSimpleName();
        }
        return frontendComponentAnnotation.value();
    }

    protected Map<String, Method> getApiMethodMapper(Class<? extends Component> componentClass) {
        Map<String, Method> actionMethodMap = new HashMap<>();
        Arrays.stream(this.basicComponentClass.getDeclaredMethods())
                .filter(method -> method.getDeclaredAnnotation(UIApi.class) != null)
                .forEach(method -> {
                    Method declaredMethod = Arrays.stream(componentClass.getDeclaredMethods())
                            .filter(m -> !m.isBridge() && m.getName().equals(method.getName()))
                            .findFirst().orElseThrow(() -> new RuntimeException("never happened"));
                    actionMethodMap.put(declaredMethod.getName(), declaredMethod);
                });
        Arrays.stream(componentClass.getDeclaredMethods())
                .filter(method -> Arrays.stream(method.getDeclaredAnnotations())
                        .anyMatch(annotation -> annotation.annotationType().getDeclaredAnnotation(UIApi.class) != null)
                ).forEach(method -> actionMethodMap.put(method.getName(), method));
        return actionMethodMap;
    }

    protected Type[] getGenericTypes(Class<T> componentClass) {
        Type superInterface = Arrays.stream(componentClass.getGenericInterfaces())
                .filter(i -> ((ParameterizedType) i).getRawType() == this.basicComponentClass)
                .findFirst().orElseThrow(() -> new RuntimeException("never happened"));
        if (!(superInterface instanceof ParameterizedType)) {
            throw new RuntimeException("never happened");
        }
        return ((ParameterizedType) superInterface).getActualTypeArguments();
    }

    protected List<FieldInfo> buildFieldInfoFromType(Type type) {
        return TypeCompileUtils.compileTypeFields(type, FieldInfo::new);
    }

    protected ReferencedComponentInfo getReferencedComponentInfo(Class<? extends Component> componentClass) {
        ReferencedComponentInfo_AutoValue refComponentInfo = new ReferencedComponentInfo_AutoValue();
        String path = componentClass.getPackage().getName().replace("\\.", "/") + componentClass.getSimpleName();
        refComponentInfo.componentClass(componentClass);
        refComponentInfo.componentPackage("@");
        refComponentInfo.componentPath(path);
        refComponentInfo.componentName(componentClass.getSimpleName());
        return refComponentInfo;
    }

    @Override
    public boolean support(Class<?> componentClass) {
        return this.basicComponentClass.isAssignableFrom(componentClass);
    }

    protected Class<?> getBasicComponentClass() {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        while (genericSuperclass != null) {
            if (genericSuperclass instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
                if (parameterizedType.getRawType() instanceof Class
                        && ComponentCompiler.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())
                ) {
                    Type componentType = parameterizedType.getActualTypeArguments()[0];
                    if (componentType instanceof Class) {
                        return (Class<?>) componentType;
                    } else if (componentType instanceof ParameterizedType && ((ParameterizedType) componentType).getRawType() instanceof Class) {
                        return (Class<?>) ((ParameterizedType) componentType).getRawType();
                    }
                }
                genericSuperclass = parameterizedType.getRawType();
            } else if (genericSuperclass instanceof Class) {
                genericSuperclass = ((Class<?>) genericSuperclass).getGenericSuperclass();
            }
        }
        throw new RuntimeException("can't happened");
    }
}
