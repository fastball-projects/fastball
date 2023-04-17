package dev.fastball.portal.model.entity;

import dev.fastball.portal.dict.PermissionType;
import dev.fastball.portal.model.context.Permission;

public interface PermissionEntity extends Permission {

    void setParentId(String parentId);

    void setCode(String code);

    void setName(String name);

    void setType(PermissionType type);

    void setTarget(String target);

    void setDescription(String description);
}
