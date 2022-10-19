package com.rocket.config.oauth2.repository;

import com.rocket.auth.entitiy.RedisAuthToken;
import org.springframework.data.repository.CrudRepository;

public interface RedisAuthTokenRepository extends CrudRepository<RedisAuthToken, String> {
}
