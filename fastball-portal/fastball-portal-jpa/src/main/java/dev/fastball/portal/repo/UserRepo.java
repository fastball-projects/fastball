package dev.fastball.portal.repo;

import dev.fastball.portal.entity.JpaUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<JpaUserEntity, String> {

    JpaUserEntity findByUsername(String username);

    JpaUserEntity findByMobile(String mobile);
}
