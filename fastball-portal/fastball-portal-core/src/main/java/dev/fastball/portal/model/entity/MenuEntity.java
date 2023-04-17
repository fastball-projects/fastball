package dev.fastball.portal.model.entity;

import dev.fastball.portal.model.context.Menu;

import java.util.Map;

public interface MenuEntity extends Menu {

    void setParentId(String parentId);

    void setCode(String code);

    void setName(String name);

    void setPath(String path);

    void setDescription(String description);

    void setParams(Map<String, Object> params);
}
