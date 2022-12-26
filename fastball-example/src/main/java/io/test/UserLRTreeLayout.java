package io.test;

import dev.fastball.core.annotation.UIComponent;
import dev.fastball.ui.components.layout.LayoutComponent;
import org.springframework.stereotype.Component;

/**
 * @author gr@fastball.dev
 * @since 2022/12/19
 */
@UIComponent("UserLRTreeForm")
@Component
@LayoutComponent.LeftAndRight(
        left = UserTree.class,
        right = UserForm.class
)
public class UserLRTreeLayout implements LayoutComponent {
}
