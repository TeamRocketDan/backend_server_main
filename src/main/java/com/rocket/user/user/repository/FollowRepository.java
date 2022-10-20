package com.rocket.user.user.repository;

import com.rocket.user.user.entity.Follow;
import com.rocket.user.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowing(User follower, User following);

    @Modifying
    @Query("delete from Follow f " +
            "where f.follower = :follower " +
            "and f.following = :following")
    void deleteByFollowerAndFollowing(
            @Param("follower") User follower,
            @Param("following") User following);
}
