package dev.fastball.core.component;

import dev.fastball.core.annotation.UIApi;
import dev.fastball.core.annotation.UIComponent;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
public class ComponentRegistry {

    private final Map<String, ComponentBean> componentBeanMap = new ConcurrentHashMap<>();

    public ComponentBean getComponentBean(String componentKey) {
        return componentBeanMap.get(componentKey);
    }

    public void register(Component component) {
        Class<? extends Component> componentClass = component.getClass();
        ComponentBean componentBean = new ComponentBean();
        componentBean.setComponent(component);
        UIComponent frontendComponentAnnotation = componentClass.getAnnotation(UIComponent.class);
        String componentKey = frontendComponentAnnotation.value();
        if (frontendComponentAnnotation.value().isEmpty()) {
            componentKey = componentClass.getSimpleName();
        }

        componentBean.setMethodMap(getApiMethodMapper(component.getClass()));
        componentBeanMap.put(componentKey, componentBean);
    }

    private Map<String, Method> getApiMethodMapper(Class<? extends Component> componentClass) {
        Set<Method> superMethodSet = new HashSet<>();
        loadAllMethod(componentClass, superMethodSet);
        Map<String, Method> actionMethodMap = new HashMap<>();
        superMethodSet.stream().filter(method -> method.getDeclaredAnnotation(UIApi.class) != null)
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

    private void loadAllMethod(Class<?> componentClass, Set<Method> methodSet) {
        if (componentClass == null) {
            return;
        }
        Arrays.stream(componentClass.getDeclaredMethods())
                .filter(method -> method.getDeclaredAnnotation(UIApi.class) != null)
                .forEach(methodSet::add);
        loadAllMethod(componentClass.getSuperclass(), methodSet);
        Arrays.stream(componentClass.getInterfaces()).forEach(anInterface -> loadAllMethod(anInterface, methodSet));
    }
}
