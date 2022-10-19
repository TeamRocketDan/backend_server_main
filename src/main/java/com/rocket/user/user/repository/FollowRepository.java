package com.rocket.user.user.repository;

import com.rocket.user.user.entity.Follow;
import com.rocket.user.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
}
