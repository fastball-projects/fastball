package dev.fastball.portal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.fastball.portal.dict.PermissionType;
import dev.fastball.portal.model.entity.PermissionEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "fb_permission")
@EqualsAndHashCode(callSuper = true)
public class JpaPermissionEntity extends JpaBaseEntity implements PermissionEntity {

    private String parentId;
    private String code;
    private String name;
    private String target;
    @Enumerated(EnumType.STRING)
    private PermissionType type;
    private String description;

    @JsonIgnore
    @ManyToMany(mappedBy = "permissions")
    private List<JpaRoleEntity> roles;
}
