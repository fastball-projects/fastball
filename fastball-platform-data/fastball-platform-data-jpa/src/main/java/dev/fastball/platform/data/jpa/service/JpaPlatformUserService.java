package dev.fastball.platform.data.jpa.service;

import dev.fastball.core.exception.BusinessException;
import dev.fastball.platform.data.jpa.entity.JpaThirdPartyUserEntity;
import dev.fastball.platform.data.jpa.entity.JpaUserEntity;
import dev.fastball.platform.data.jpa.repo.ThirdPartyUserRepo;
import dev.fastball.platform.data.jpa.repo.UserRepo;
import dev.fastball.platform.dict.UserStatus;
import dev.fastball.platform.entity.ThirdPartyUser;
import dev.fastball.platform.entity.User;
import dev.fastball.platform.entity.UserWithPassword;
import dev.fastball.platform.exception.UserNotFoundException;
import dev.fastball.platform.model.RegisterUser;
import dev.fastball.platform.service.PlatformUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;


@RequiredArgsConstructor
public class JpaPlatformUserService implements PlatformUserService {

    private final UserRepo userRepo;
    private final ThirdPartyUserRepo thirdPartyUserRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(RegisterUser user) {
        JpaUserEntity userEntity = new JpaUserEntity();
        userEntity.setUsername(user.getUsername());
        userEntity.setNickname(user.getNickname());
        userEntity.setMobile(user.getMobile());
        userEntity.setStatus(UserStatus.Enabled);
        if (user.getPassword() != null) {
            userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepo.save(userEntity);
        return userEntity;
    }

    @Override
    public void changePassword(Long userId, String password, String newPassword) {
        Optional<JpaUserEntity> userOptional = userRepo.findById(userId);
        if (userOptional.isEmpty()) {
            return;
        }
        JpaUserEntity user = userOptional.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }

    @Override
    public boolean resetPasswordByUserId(Long userId, String password) {
        Optional<JpaUserEntity> userOptional = userRepo.findById(userId);
        if (userOptional.isEmpty()) {
            return false;
        }
        JpaUserEntity user = userOptional.get();
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(user.getNickname());
        user.setLastUpdatedAt(new Date());
        userRepo.save(user);
        return true;
    }

    @Override
    public boolean resetPasswordByUserName(String username, String password) {
        JpaUserEntity user = userRepo.findByUsername(username);
        if (user == null) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(user.getNickname());
        user.setLastUpdatedAt(new Date());
        userRepo.save(user);
        return true;
    }

    @Override
    public boolean resetPasswordByMobile(String mobile, String password) {
        JpaUserEntity user = userRepo.findByMobile(mobile);
        if (user == null) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(user.getNickname());
        user.setLastUpdatedAt(new Date());
        userRepo.save(user);
        return true;
    }

    @Override
    public User loadByUserId(Long userId) {
        return copyUser(userRepo.findById(userId).orElse(null));
    }

    @Override
    public UserWithPassword loadByUsernameWithPassword(String username) {
        return copyUser(userRepo.findByUsername(username), true);
    }

    @Override
    public User loadByUsername(String username) {
        return copyUser(userRepo.findByUsername(username));
    }

    @Override
    public User loadByMobile(String mobile) {
        return copyUser(userRepo.findByMobile(mobile));
    }

    @Override
    public ThirdPartyUser loadByThirdParty(String thirdPartyPlatform, String outId) {
        return thirdPartyUserRepo.findByThirdPartyPlatformAndOutId(thirdPartyPlatform, outId);
    }

    @Override
    public ThirdPartyUser bindThirdPartyUser(Long userId, String thirdPartyPlatform, String outId) {
        JpaThirdPartyUserEntity thirdPartyUser = new JpaThirdPartyUserEntity(userId, thirdPartyPlatform, outId);
        return thirdPartyUserRepo.save(thirdPartyUser);
    }

    @Override
    public UserStatus getUserStatus(Long userId) throws UserNotFoundException {
        Optional<JpaUserEntity> userOptional = userRepo.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException();
        }
        return userOptional.get().getStatus();
    }

    @Override
    public boolean enableUser(Long userId) {
        Optional<JpaUserEntity> userOptional = userRepo.findById(userId);
        if (userOptional.isEmpty()) {
            return false;
        }
        JpaUserEntity user = userOptional.get();
        user.setStatus(UserStatus.Enabled);
        user.setNickname(user.getNickname());
        user.setLastUpdatedAt(new Date());
        userRepo.save(user);
        return true;
    }

    @Override
    public boolean disableUser(Long userId) {
        Optional<JpaUserEntity> userOptional = userRepo.findById(userId);
        if (userOptional.isEmpty()) {
            return false;
        }
        JpaUserEntity user = userOptional.get();
        user.setStatus(UserStatus.Disabled);
        user.setNickname(user.getNickname());
        user.setLastUpdatedAt(new Date());
        userRepo.save(user);
        return true;
    }

    private JpaUserEntity copyUser(User user) {
        return copyUser(user, false);
    }

    private JpaUserEntity copyUser(User user, boolean withPassword) {
        if (user == null) {
            return null;
        }
        JpaUserEntity resultUser = new JpaUserEntity();
        BeanUtils.copyProperties(user, resultUser);
        if (!withPassword) {
            resultUser.setPassword(null);
        }
        return resultUser;
    }
}
