package dev.fastball.core.component;

import dev.fastball.core.annotation.UIComponent;
import dev.fastball.core.info.ComponentInfo;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
public abstract class AbstractComponentCompiler<T, P> implements ComponentCompiler<T, P> {

    private final Class<?> componentClass = getComponentClass();

    protected abstract P compileProps(Class<T> componentClass);

    protected abstract String getComponentName();

    @Override
    public ComponentInfo<P> compile(Class<T> componentClass) {
        ComponentInfo<P> componentInfo = new ComponentInfo<>();
        UIComponent frontendComponentAnnotation = componentClass.getAnnotation(UIComponent.class);
        componentInfo.setComponentKey(frontendComponentAnnotation.value());
        componentInfo.setComponentName(getComponentName());
        P props = compileProps(componentClass);
        componentInfo.setProps(props);
        return componentInfo;
    }

    @Override
    public boolean support(Class<?> componentClass) {
        return this.componentClass.isAssignableFrom(componentClass);
    }

    protected Class<?> getComponentClass() {
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
