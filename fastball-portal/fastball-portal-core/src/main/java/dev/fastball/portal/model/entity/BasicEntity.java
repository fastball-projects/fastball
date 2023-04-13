package dev.fastball.portal.model.entity;

import java.util.Date;

public interface BasicEntity {

    Date getCreatedAt();

    void setCreatedAt(Date createdAt);

    String getCreatedBy();

    void setCreatedBy(String createdBy);

    Date getLastUpdatedAt();

    void setLastUpdatedAt(Date lastUpdatedAt);

    String getLastUpdatedBy();

    void setLastUpdatedBy(String lastUpdatedBy);

}
