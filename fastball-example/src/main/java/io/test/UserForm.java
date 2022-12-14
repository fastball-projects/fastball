package io.test;

import dev.fastball.core.annotation.UIComponent;
import dev.fastball.ui.components.form.Form;

/**
 * @author gr@fastball.dev
 * @since 2022/12/14
 */
@UIComponent("UserForm")
public class UserForm implements Form<User> {

    @Form.RecordAction("提交")
    public void submit(User user) {
        System.out.println(user);
    }
}
