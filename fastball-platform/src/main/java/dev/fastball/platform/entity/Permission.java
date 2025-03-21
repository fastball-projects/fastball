package dev.fastball.platform.entity;


public interface Permission extends IdModel {

    Long getParentId();

    String getCode();

    String getName();

    String getPlatform();

    String getPermissionType();

    String getTarget();

    String getDescription();
}
