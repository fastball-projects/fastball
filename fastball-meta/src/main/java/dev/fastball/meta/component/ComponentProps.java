package dev.fastball.meta.component;

import dev.fastball.meta.action.ActionInfo;
import dev.fastball.meta.utils.JsonUtils;

import java.util.List;
import java.util.Set;

/**
 * @author gr@fastball.dev
 * @since 2022/12/15
 */
public interface ComponentProps {
    String componentKey();

    void componentKey(String componentKey);

    @JsonUtils.JsonIgnoreOnGenerateCode
    Set<ReferencedComponentInfo> referencedComponentInfoList();

    void referencedComponentInfoList(Set<ReferencedComponentInfo> referencedComponentInfoList);

    List<ActionInfo> actions();

    void actions(List<ActionInfo> actions);

    List<ActionInfo> recordActions();

    void recordActions(List<ActionInfo> recordActions);
}
