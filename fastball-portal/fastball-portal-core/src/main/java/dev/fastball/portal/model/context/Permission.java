package dev.fastball.portal.model.context;

import dev.fastball.portal.dict.PermissionType;

public interface Permission extends IdModel {

    String getParentId();

    String getCode();

    String getName();

    PermissionType getType();

    String getTarget();

    String getDescription();
}
