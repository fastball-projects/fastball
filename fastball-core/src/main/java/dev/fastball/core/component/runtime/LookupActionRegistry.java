package dev.fastball.core.component.runtime;

import dev.fastball.core.annotation.UIComponent;
import dev.fastball.core.component.LookupActionComponent;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static dev.fastball.core.Constants.LOOKUP_ACTION_METHOD_NAME;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
public class LookupActionRegistry {

    private final Map<String, LookupActionBean> lookupActionBeanMap = new ConcurrentHashMap<>();

    public LookupActionBean getLookupActionBean(String lookupActionKey) {
        return lookupActionBeanMap.get(lookupActionKey);
    }

    public void register(LookupActionComponent lookupAction) {
        Class<? extends LookupActionComponent> lookupActionClass = lookupAction.getClass();
        LookupActionBean lookupActionBean = new LookupActionBean();
        lookupActionBean.setLookupAction(lookupAction);
        UIComponent frontendComponentAnnotation = lookupActionClass.getAnnotation(UIComponent.class);
        String lookupActionKey = frontendComponentAnnotation.value();
        if (frontendComponentAnnotation.value().isEmpty()) {
            lookupActionKey = lookupActionClass.getSimpleName();
        }
        lookupActionBean.setLookupMethod(getLookupActionMethod(lookupActionClass));
        lookupActionBean.setLookupActionKey(lookupActionKey);
        lookupActionBeanMap.put(lookupActionKey, lookupActionBean);
    }

    private Method getLookupActionMethod(Class<?> lookupActionClass) {
        if (lookupActionClass == null || lookupActionClass == Object.class) {
            return null;
        }
        for (Method declaredMethod : lookupActionClass.getDeclaredMethods()) {
            if (declaredMethod.getName().equals(LOOKUP_ACTION_METHOD_NAME)) {
                return declaredMethod;
            }
        }
        return getLookupActionMethod(lookupActionClass.getSuperclass());
    }
}
