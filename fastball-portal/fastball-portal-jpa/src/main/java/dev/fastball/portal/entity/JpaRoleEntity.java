package dev.fastball.portal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.fastball.orm.jpa.JpaBaseEntity;
import dev.fastball.portal.model.entity.RoleEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "fb_role")
@EqualsAndHashCode(callSuper = true)
public class JpaRoleEntity extends JpaBaseEntity implements RoleEntity {

    private String code;
    private String name;
    private String description;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "fb_role_permission_rel", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private List<JpaPermissionEntity> permissions;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private List<JpaUserEntity> users;
}
