package io.test;

import dev.fastball.core.annotation.UIComponent;
import dev.fastball.ui.components.layout.LayoutComponent;
import org.springframework.stereotype.Component;

/**
 * @author gr@fastball.dev
 * @since 2022/12/19
 */
@UIComponent("UserTableForm")
@Component
@LayoutComponent.TopAndBottom(
        top = UserTable.class,
        bottom = UserForm.class
)
public class UserLayout implements LayoutComponent {
}
