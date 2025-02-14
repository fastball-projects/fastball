package dev.fastball.platform.core.model.context;

public interface Menu extends IdModel {
    Long getParentId();

    String getCode();

    String getName();

    String getPath();

    String getDescription();

    Object getParams();
}
