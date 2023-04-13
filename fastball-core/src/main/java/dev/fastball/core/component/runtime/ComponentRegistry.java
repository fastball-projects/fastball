package dev.fastball.core.component.runtime;

import dev.fastball.core.annotation.RecordAction;
import dev.fastball.core.annotation.UIApi;
import dev.fastball.core.annotation.UIComponent;
import dev.fastball.core.component.Component;
import dev.fastball.core.component.RecordActionFilter;
import org.springframework.aop.support.AopUtils;

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
        Class<? extends Component> componentClass = (Class<? extends Component>) AopUtils.getTargetClass(component);
        ComponentBean componentBean = new ComponentBean();
        componentBean.setComponent(component);
        UIComponent frontendComponentAnnotation = componentClass.getAnnotation(UIComponent.class);
        String componentKey = frontendComponentAnnotation.value();
        if (frontendComponentAnnotation.value().isEmpty()) {
            componentKey = componentClass.getSimpleName();
        }

        componentBean.setMethodMap(getApiMethodMapper(component.getClass()));
        componentBean.setRecordActionFilterClasses(getRecordActionFilter(component.getClass()));
        componentBeanMap.put(componentKey, componentBean);
    }

    private Map<String, UIApiMethod> getApiMethodMapper(Class<? extends Component> componentClass) {
        Set<Method> superMethodSet = new HashSet<>();
        loadAllMethod(componentClass, superMethodSet);
        Map<String, UIApiMethod> actionMethodMap = new HashMap<>();
        superMethodSet.stream().filter(method -> method.getDeclaredAnnotation(UIApi.class) != null)
                .forEach(method -> {
                    UIApi uiApi = method.getDeclaredAnnotation(UIApi.class);
                    Method declaredMethod = Arrays.stream(componentClass.getDeclaredMethods())
                            .filter(m -> !m.isBridge() && m.getName().equals(method.getName()))
                            .findFirst().orElseThrow(() -> new RuntimeException("never happened"));
                    actionMethodMap.put(declaredMethod.getName(), buildUIApiMethod(declaredMethod, uiApi));
                });
        Arrays.stream(componentClass.getDeclaredMethods())
                .filter(method -> Arrays.stream(method.getDeclaredAnnotations())
                        .anyMatch(annotation -> annotation.annotationType().getDeclaredAnnotation(UIApi.class) != null)
                ).forEach(method -> actionMethodMap.put(method.getName(), buildUIApiMethod(method, null)));
        return actionMethodMap;
    }

    private Map<String, Class<? extends RecordActionFilter>> getRecordActionFilter(Class<? extends Component> componentClass) {
        Map<String, Class<? extends RecordActionFilter>> recordActionFilterMap = new HashMap<>();
        for (Method method : componentClass.getDeclaredMethods()) {
            RecordAction recordAction = method.getDeclaredAnnotation(RecordAction.class);
            if (recordAction != null) {
                String actionKey = recordAction.key().isEmpty() ? method.getName() : recordAction.key();
                recordActionFilterMap.put(actionKey, recordAction.recordActionFilter());
            }
        }
        return recordActionFilterMap;
    }

    private UIApiMethod buildUIApiMethod(Method method, UIApi uiApi) {
        UIApiMethod.UIApiMethodBuilder methodBuilder = UIApiMethod.builder().key(method.getName()).method(method);
        if (uiApi != null) {
            methodBuilder.needRecordFilter(uiApi.needRecordFilter());
        }
        return methodBuilder.build();
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
