package io.test;

import lombok.Data;

import java.util.Date;

/**
 * @author gr@fastball.dev
 * @since 2022/12/10
 */
@Data
public class User {

    private String name;

    private int age;

    private Date createdAt;
}
