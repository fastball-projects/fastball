package dev.fastball.portal.entity;

import dev.fastball.portal.model.entity.BasicEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public abstract class JpaBaseEntity implements BasicEntity {

    @Id
    private String id;
    private Date createdAt;
    private String createdBy;
    private Date lastUpdatedAt;
    private String lastUpdatedBy;
}
