package dev.fastball.core.component.runtime;

import dev.fastball.core.component.RecordActionFilter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gr@fastball.dev
 * @since 2023/1/29
 */
public class RecordActionFilterRegistry {
    private final Map<Class<? extends RecordActionFilter>, RecordActionFilter> recordActionFilterBeanMap = new ConcurrentHashMap<>();

    public void register(RecordActionFilter recordActionFilter) {
        recordActionFilterBeanMap.put(recordActionFilter.getClass(), recordActionFilter);
    }

    // TODO 临时处理一下, 逻辑上还是要有生命周期的声明
    public RecordActionFilter getRecordActionFilter(Class<? extends RecordActionFilter> recordActionFilterClass) {
        RecordActionFilter recordActionFilter = recordActionFilterBeanMap.get(recordActionFilterClass);
        if (recordActionFilter == null) {
            try {
                recordActionFilter = recordActionFilterClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                return null;
            }
            recordActionFilterBeanMap.put(recordActionFilterClass, recordActionFilter);
        }
        return recordActionFilter;
    }

}
