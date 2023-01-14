package dev.fastball.runtime.spring;

import dev.fastball.core.component.Component;
import dev.fastball.core.component.LookupActionComponent;
import dev.fastball.core.component.runtime.ComponentRegistry;
import dev.fastball.core.component.runtime.LookupActionRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author gengrong
 */
@RequiredArgsConstructor
public class FastballComponentPostProcessor implements BeanPostProcessor {
    private final ComponentRegistry componentRegistry;
    private final LookupActionRegistry lookupActionRegistry;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Component) {
            componentRegistry.register((Component) bean);
        }
        if (bean instanceof LookupActionComponent) {
            lookupActionRegistry.register((LookupActionComponent) bean);
        }
        return bean;
    }
}
