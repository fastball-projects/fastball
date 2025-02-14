package dev.fastball.platform.core.model.entity;

import dev.fastball.platform.core.model.context.IdModel;

public interface ThirdPartyLoginEntity extends IdModel {

    Long getUserId();

    void setUserId(Long userId);

    String getType();

    void setType(String type);

    String getOuterId();

    void setOuterId(String outerId);

    String getInfo();

    void setInfo(String info);
}
