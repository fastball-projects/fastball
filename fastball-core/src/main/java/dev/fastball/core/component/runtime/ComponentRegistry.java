package dev.fastball.core.component.runtime;

import dev.fastball.core.annotation.*;
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
    private final Set<String> anonymousAccessComponentBeans = new HashSet<>();

    public Set<String> getAnonymousAccessComponentBeans() {
        return anonymousAccessComponentBeans;
    }

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
        AnonymousAccess anonymousAccessAnnotation = componentClass.getAnnotation(AnonymousAccess.class);
        if(anonymousAccessAnnotation != null) {
            componentBean.setAnonymousAccess(true);
            anonymousAccessComponentBeans.add(componentKey);
        }
        componentBean.setMethodMap(getApiMethodMapper(componentClass));
        componentBean.setRecordActionFilterClasses(getRecordActionFilter(componentClass));
        componentBeanMap.put(componentKey, componentBean);
    }

    private Map<String, UIApiMethod> getApiMethodMapper(Class<? extends Component> componentClass) {
        Set<Method> superMethodSet = new LinkedHashSet<>();
        loadAllMethod(componentClass, superMethodSet);
        Map<String, UIApiMethod> actionMethodMap = new HashMap<>();
        superMethodSet.stream().filter(method -> method.getDeclaredAnnotation(UIApi.class) != null)
                .forEach(method -> {
                    UIApi uiApi = method.getDeclaredAnnotation(UIApi.class);
                    Method declaredMethod = Arrays.stream(componentClass.getDeclaredMethods())
                            .filter(m -> !m.isBridge() && m.getName().equals(method.getName()))
                            .findFirst().orElseGet(() -> method.isDefault() ? method : null);
//                    if (declaredMethod == null) {
//                        throw new RuntimeException("never happened");
//                    }
                    UIApiMethod uiApiMethod = buildUIApiMethod(declaredMethod != null ? declaredMethod : method, uiApi);
                    actionMethodMap.put(uiApiMethod.getKey(), uiApiMethod);
                });
        Arrays.stream(componentClass.getMethods())
                .filter(method -> Arrays.stream(method.getDeclaredAnnotations())
                        .anyMatch(annotation -> annotation.annotationType().getDeclaredAnnotation(UIApi.class) != null)
                ).forEach(method -> {
                    UIApiMethod uiApiMethod = buildUIApiMethod(method, null);
                    actionMethodMap.put(uiApiMethod.getKey(), uiApiMethod);
                });
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
        ViewActions viewActions = componentClass.getDeclaredAnnotation(ViewActions.class);
        if (viewActions != null) {
            for (ViewAction viewAction : viewActions.recordActions()) {
                String actionKey = viewAction.key();
                recordActionFilterMap.put(actionKey, viewAction.recordActionFilter());
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
        Arrays.stream(componentClass.getInterfaces()).forEach(anInterface -> loadAllMethod(anInterface, methodSet));
        loadAllMethod(componentClass.getSuperclass(), methodSet);
        Arrays.stream(componentClass.getDeclaredMethods())
                .filter(method -> method.getDeclaredAnnotation(UIApi.class) != null)
                .forEach(methodSet::add);
    }
}
