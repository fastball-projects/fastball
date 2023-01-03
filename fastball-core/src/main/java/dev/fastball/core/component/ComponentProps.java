package dev.fastball.core.component;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Set;

/**
 * @author gr@fastball.dev
 * @since 2022/12/15
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface ComponentProps {
    String componentKey();

    void componentKey(String componentKey);

    Set<ReferencedComponentInfo> referencedComponentInfoList();

    void referencedComponentInfoList(Set<ReferencedComponentInfo> referencedComponentInfoList);
}
