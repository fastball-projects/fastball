package io.test;

import dev.fastball.core.annotation.UIComponent;
import dev.fastball.ui.components.layout.LayoutComponent;
import org.springframework.stereotype.Component;

/**
 * @author gr@fastball.dev
 * @since 2022/12/19
 */
@UIComponent("UserLRForm")
@LayoutComponent.LeftAndRight(
        left = UserTable.class,
        right = UserForm.class
)
public class UserLRLayout implements LayoutComponent {
}
