package dev.fastball.ui.components.layout;

import dev.fastball.auto.value.annotation.AutoValue;
import dev.fastball.core.component.ComponentProps;
import dev.fastball.core.component.ReferencedComponentInfo;

/**
 * @author gr@fastball.dev
 * @since 2022/12/19
 */
public interface LayoutProps extends ComponentProps {
    LayoutType layoutType();
}

@AutoValue
interface LeftAndRightLayoutProps extends LayoutProps {
    LayoutType layoutType = LayoutType.LeftAndRight;

    ReferencedComponentInfo left();

    ReferencedComponentInfo right();
}

@AutoValue
interface TopAndBottomLayoutProps extends LayoutProps {
    LayoutType layoutType = LayoutType.TopAndBottom;

    ReferencedComponentInfo top();

    ReferencedComponentInfo bottom();
}

@AutoValue
interface LeftAndTopBottomLayoutProps extends LayoutProps {
    LayoutType layoutType = LayoutType.LeftAndTopBottom;

    ReferencedComponentInfo left();

    ReferencedComponentInfo top();

    ReferencedComponentInfo bottom();
}