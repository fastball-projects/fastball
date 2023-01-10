package dev.fastball.runtime.spring;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.fastball.core.component.LookupActionComponent;
import dev.fastball.core.component.runtime.ComponentBean;
import dev.fastball.core.component.runtime.ComponentRegistry;
import dev.fastball.core.component.runtime.LookupActionBean;
import dev.fastball.core.component.runtime.LookupActionRegistry;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/fastball")
public class FastballComponentController {
    private final ComponentRegistry componentRegistry;
    private final LookupActionRegistry lookupActionRegistry;
    private final ObjectMapper objectMapper;

    @PostMapping("/component/{componentKey}/action/{actionKey}")
    public Object invokeComponentAction(@PathVariable String componentKey, @PathVariable String actionKey, ServletRequest request) throws IOException, InvocationTargetException, IllegalAccessException {
        ComponentBean componentBean = componentRegistry.getComponentBean(componentKey);
        Method actionMethod = componentBean.getMethodMap().get(actionKey);
        return invokeActionMethod(componentBean.getComponent(), actionMethod, request);
    }

    @PostMapping("/lookup/{lookupKey}")
    public Object loadLookupItems(@PathVariable String lookupKey, ServletRequest request) throws IOException, InvocationTargetException, IllegalAccessException {
        LookupActionBean lookupActionBean = lookupActionRegistry.getLookupActionBean(lookupKey);
        if (lookupActionBean == null) {
            throw new RuntimeException("Lookup action not found");
        }
        LookupActionComponent lookupActionComponent = lookupActionBean.getLookupAction();
        Method actionMethod = lookupActionBean.getLookupMethod();
        return invokeActionMethod(lookupActionComponent, actionMethod, request);
    }


    private Object invokeActionMethod(Object bean, Method actionMethod, ServletRequest request) throws IOException, InvocationTargetException, IllegalAccessException {
        Parameter[] parameterList = actionMethod.getParameters();
        JsonNode jsonNode = objectMapper.readTree(request.getInputStream());
        Object[] params = new Object[parameterList.length];
        for (int i = 0; i < Math.min(jsonNode.size(), params.length); i++) {
            Parameter parameter = parameterList[i];
            Object param = objectMapper.readValue(jsonNode.get(i).toString(), new TypeReference<Object>() {
                @Override
                public Type getType() {
                    return parameter.getParameterizedType();
                }
            });
            params[i] = param;
        }
        return actionMethod.invoke(bean, params);
    }
}
