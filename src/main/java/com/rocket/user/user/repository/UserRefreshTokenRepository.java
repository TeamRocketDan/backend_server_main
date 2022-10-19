package com.rocket.user.user.repository;

import com.rocket.user.user.entity.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {
    Optional<UserRefreshToken> findByUuid(String uuid);
    boolean existsByUuid(String uuid);
    Optional<UserRefreshToken> findByUuidAndRefreshToken(String uuid, String refreshToken);
}
