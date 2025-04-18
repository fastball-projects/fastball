package dev.fastball.platform.service;


import dev.fastball.platform.feature.business.context.BusinessContextAccessor;

public interface BusinessContextService {

    BusinessContextAccessor<?> getBusinessContext(String contextKey);

    BusinessContextAccessor<?> getBusinessContext(Class<? extends BusinessContextAccessor<?>> contextClass);
}
