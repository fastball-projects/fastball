package dev.fastball.platform.data.jpa.repo;

import dev.fastball.platform.data.jpa.entity.JpaThirdPartyUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdPartyUserRepo extends JpaRepository<JpaThirdPartyUserEntity, Long> {
    JpaThirdPartyUserEntity findByThirdPartyPlatformAndOutId(String thirdPartyPlatform, String outId);
}
