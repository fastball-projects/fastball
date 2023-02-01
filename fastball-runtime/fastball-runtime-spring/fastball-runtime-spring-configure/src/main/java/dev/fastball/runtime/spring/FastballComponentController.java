package dev.fastball.runtime.spring;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.fastball.core.component.DataRecord;
import dev.fastball.core.component.DataResult;
import dev.fastball.core.component.LookupActionComponent;
import dev.fastball.core.component.RecordActionFilter;
import dev.fastball.core.component.runtime.*;
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
import java.util.HashMap;
import java.util.Map;

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
    private final RecordActionFilterRegistry recordActionFilterRegistry;
    private final ObjectMapper objectMapper;

    @PostMapping("/component/{componentKey}/action/{actionKey}")
    public Object invokeComponentAction(@PathVariable String componentKey, @PathVariable String actionKey, ServletRequest request) throws IOException, InvocationTargetException, IllegalAccessException {
        ComponentBean componentBean = componentRegistry.getComponentBean(componentKey);
        UIApiMethod actionMethod = componentBean.getMethodMap().get(actionKey);
        Object result = invokeActionMethod(componentBean.getComponent(), actionMethod.getMethod(), request);
        if (actionMethod.isNeedRecordFilter()) {
            if (result instanceof DataResult) {
                DataResult<?> dataResult = (DataResult<?>) result;
                dataResult.getData().stream().filter(DataRecord.class::isInstance)
                        .forEach(dataRecord -> doRecordActionFilter((DataRecord) dataRecord, componentBean));
            } else if (result instanceof DataRecord) {
                doRecordActionFilter((DataRecord) result, componentBean);
            }
        }
        return result;
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

    private void doRecordActionFilter(DataRecord record, ComponentBean componentBean) {
        Map<String, Boolean> recordActionAvailableFlags = new HashMap<>();
        componentBean.getRecordActionFilterClasses().forEach((key, value) -> {
            RecordActionFilter recordActionFilter = recordActionFilterRegistry.getRecordActionFilter(value);
            if (recordActionFilter != null) {
                recordActionAvailableFlags.put(key, recordActionFilter.filter(record));
            }
        });
        record.setRecordActionAvailableFlags(recordActionAvailableFlags);
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
