package dev.fastball.portal.model.entity;

import dev.fastball.portal.model.context.Role;

public interface RoleEntity extends Role {

    void setCode(String code);

    void setName(String name);

    void setDescription(String description);
}
