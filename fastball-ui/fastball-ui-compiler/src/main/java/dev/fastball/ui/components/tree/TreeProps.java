package dev.fastball.ui.components.tree;

import dev.fastball.auto.value.annotation.AutoValue;
import dev.fastball.ui.common.ActionInfo;
import dev.fastball.ui.common.ComponentProps;

import java.util.List;

/**
 * @author gr@fastball.dev
 * @since 2022/12/20
 */
@AutoValue
public interface TreeProps extends ComponentProps {

    String headerTitle();

    List<ActionInfo> actions();
}
