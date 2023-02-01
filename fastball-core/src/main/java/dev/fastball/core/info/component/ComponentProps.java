package dev.fastball.core.info.component;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dev.fastball.core.info.action.ActionInfo;
import dev.fastball.core.utils.JsonUtils;

import java.util.List;
import java.util.Set;

/**
 * @author gr@fastball.dev
 * @since 2022/12/15
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
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
