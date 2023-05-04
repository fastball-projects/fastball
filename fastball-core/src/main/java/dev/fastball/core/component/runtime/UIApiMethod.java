package dev.fastball.core.component.runtime;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/**
 * @author gr@fastball.dev
 * @since 2023/1/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UIApiMethod {
    private String key;
    private Method method;
    private boolean needRecordFilter;
    private boolean downloadApi;
}
