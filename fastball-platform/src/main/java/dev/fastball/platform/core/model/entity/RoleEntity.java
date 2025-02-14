package dev.fastball.platform.core.model.entity;

import dev.fastball.platform.core.model.context.Role;

public interface RoleEntity extends Role {

    void setCode(String code);

    void setName(String name);

    void setDescription(String description);
}
