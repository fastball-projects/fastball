package dev.fastball.ui.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Set;

/**
 * @author gr@fastball.dev
 * @since 2022/12/15
 */
public interface ComponentProps {
    String componentKey();

    @JsonIgnore
    Set<ReferencedComponentInfo> referencedComponentInfoList();
    void referencedComponentInfoList(Set<ReferencedComponentInfo> referencedComponentInfoList);
}
