package io.test;

import dev.fastball.ui.annotation.Field;
import dev.fastball.ui.components.tree.TreeNode;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author gr@fastball.dev
 * @since 2022/12/10
 */
@Data
public class User {

    private Long id;


    @Field(title = "姓名1", tips = "用户姓名")
    @Size(min = 6, max = 64, message = "name length between [6 - 64]")
    @NotNull(message = "name can not be null")
    private String name;

    @Min(value = 0, message = "age >= 0")
    @Max(value = 199, message = "age < 199")
    private int age;

    private Date createdAt;

    private List<User> children;
}
