package dev.fastball.platform.service.support;

import dev.fastball.platform.feature.business.context.BusinessContextAccessor;
import dev.fastball.platform.service.BusinessContextService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class DefaultBusinessContextService implements BusinessContextService {
    private final Map<String, BusinessContextAccessor<?>> accessorKeyMap = new ConcurrentHashMap<>();
    private final Map<Class<? extends BusinessContextAccessor>, BusinessContextAccessor<?>> accessorClassMap = new ConcurrentHashMap<>();

    public DefaultBusinessContextService(List<BusinessContextAccessor<?>> accessorList) {
        for (BusinessContextAccessor<?> businessContextAccessor : accessorList) {
            accessorKeyMap.put(businessContextAccessor.contextKey(), businessContextAccessor);
            accessorClassMap.put(businessContextAccessor.getClass(), businessContextAccessor);
        }
    }

    @Override
    public BusinessContextAccessor<?> getBusinessContext(String contextKey) {
        return accessorKeyMap.get(contextKey);
    }

    @Override
    public BusinessContextAccessor<?> getBusinessContext(Class<? extends BusinessContextAccessor<?>> contextClass) {
        return accessorClassMap.get(contextClass);
    }
}
