package io.test;

import dev.fastball.core.annotation.UIComponent;
import dev.fastball.ui.components.table.Table;
import dev.fastball.ui.components.table.TableDataResult;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;

/**
 * @author gr@fastball.dev
 * @since 2022/12/10
 */
@UIComponent("User-Table")
@Component
public class UserTable implements Table<User, UserQuerier> {
    @Override
    public TableDataResult<User> loadData(UserQuerier querier) {
        User user = new User();
        user.setName("abc");
        user.setAge(17);
        user.setCreatedAt(new Date());
        return TableDataResult.<User>builder().data(Collections.singleton(user)).build();
    }
}
