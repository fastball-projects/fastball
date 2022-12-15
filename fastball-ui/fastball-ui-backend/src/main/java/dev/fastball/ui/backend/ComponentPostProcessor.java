package dev.fastball.ui.backend;

import dev.fastball.core.component.Component;
import dev.fastball.core.component.ComponentRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author gengrong
 */
@RequiredArgsConstructor
public class ComponentPostProcessor implements BeanPostProcessor {

    private final ComponentRegistry componentRegistry;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Component) {
            componentRegistry.register((Component) bean);
        }
        return bean;
    }
}
