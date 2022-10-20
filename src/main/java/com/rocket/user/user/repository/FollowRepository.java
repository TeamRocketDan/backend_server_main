package com.rocket.user.user.repository;

import com.rocket.user.user.entity.Follow;
import com.rocket.user.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowing(User follower, User following);
}
