package dev.fastball.platform.core.model.entity;

import dev.fastball.platform.core.model.context.Menu;

public interface MenuEntity extends Menu {

    void setParentId(Long parentId);

    void setCode(String code);

    void setName(String name);

    void setPath(String path);

    void setDescription(String description);

    void setParams(Object params);
}
