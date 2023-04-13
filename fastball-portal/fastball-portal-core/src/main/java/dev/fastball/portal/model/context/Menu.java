package dev.fastball.portal.model.context;

import java.util.Map;

public interface Menu extends IdModel {
    String getParentId();

    String getCode();

    String getName();

    String getPath();

    String getDescription();

    Map<String, Object> getParams();
}
