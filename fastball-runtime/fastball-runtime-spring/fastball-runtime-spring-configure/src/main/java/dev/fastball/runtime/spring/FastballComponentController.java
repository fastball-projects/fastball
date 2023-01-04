package dev.fastball.runtime.spring;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.fastball.core.component.runtime.ComponentBean;
import dev.fastball.core.component.runtime.ComponentRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * @author gr@fastball.dev
 * @since 2022/12/15
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/fastball/component")
public class FastballComponentController {
    private final ComponentRegistry componentRegistry;
    private final ObjectMapper objectMapper;

    @PostMapping("/{componentKey}/action/{actionKey}")
    public Object invokeComponentAction(@PathVariable String componentKey, @PathVariable String actionKey, ServletRequest request) throws IOException, BindException, InvocationTargetException, IllegalAccessException {
        ComponentBean componentBean = componentRegistry.getComponentBean(componentKey);
        Method actionMethod = componentBean.getMethodMap().get(actionKey);
        Parameter[] parameterList = actionMethod.getParameters();
        JsonNode jsonNode = objectMapper.readTree(request.getInputStream());
        Object[] params = new Object[jsonNode.size()];
        for (int i = 0; i < jsonNode.size(); i++) {
            Parameter parameter = parameterList[i];
            Object param = objectMapper.readValue(jsonNode.get(i).toString(), new TypeReference<Object>() {
                @Override
                public Type getType() {
                    return parameter.getParameterizedType();
                }
            });
//            DataBinder binder = new DataBinder(param, parameter.getName());
//            binder.addValidators(validatorFactoryBean);
//            binder.validate();
//            if (binder.getBindingResult().hasErrors()) {
//                throw new BindException(binder.getBindingResult());
//            }
            params[i] = param;
        }
        return actionMethod.invoke(componentBean.getComponent(), params);
    }
}
