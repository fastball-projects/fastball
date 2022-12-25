package dev.fastball.ui.common;

import java.util.Set;

/**
 * @author gr@fastball.dev
 * @since 2022/12/15
 */
public interface ComponentProps {
    String componentKey();

    Set<ReferencedComponentInfo> referencedComponentInfoList();
    void referencedComponentInfoList(Set<ReferencedComponentInfo> referencedComponentInfoList);
}
