package dev.fastball.platform.core.model.entity;

import dev.fastball.platform.core.model.context.Permission;

public interface PermissionEntity extends Permission {

    void setParentId(Long parentId);

    void setCode(String code);

    void setName(String name);

    void setPlatform(String platform);

    void setPermissionType(String permissionType);

    void setTarget(String target);

    void setDescription(String description);
}
