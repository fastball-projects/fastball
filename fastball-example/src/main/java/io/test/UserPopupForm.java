package io.test;

import dev.fastball.core.annotation.UIComponent;
import dev.fastball.ui.annotation.RecordAction;
import dev.fastball.ui.components.form.PopupForm;
import dev.fastball.ui.PopupType;
import org.springframework.stereotype.Component;

/**
 * @author gr@fastball.dev
 * @since 2022/12/14
 */
@UIComponent("UserPopupForm")
@Component
@PopupForm.Config(popupType = PopupType.Modal)
public class UserPopupForm implements PopupForm<User> {

    @RecordAction("提交")
    @CloseOnSuccess
    public void submit(User user) {
        System.out.println(user);
        UserTable.age = user.getAge();
    }
}
