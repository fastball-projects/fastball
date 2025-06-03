package dev.fastball.platform.data.jpa.entity;

import dev.fastball.core.annotation.Field;
import dev.fastball.orm.jpa.JpaBaseEntity;
import dev.fastball.platform.entity.ThirdPartyUser;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fb_third_party_user")
@EqualsAndHashCode(callSuper = true)
public class JpaThirdPartyUserEntity extends JpaBaseEntity implements ThirdPartyUser {
    @Field(title = "用户")
    private Long userId;
    @Field(title = "外部 ID")
    private String outId;
    @Field(title = "三方平台")
    private String thirdPartyPlatform;
}
