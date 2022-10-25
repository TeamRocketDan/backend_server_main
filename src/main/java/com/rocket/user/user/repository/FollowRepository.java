package com.rocket.user.user.repository;

import com.rocket.user.user.entity.Follow;
import com.rocket.user.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(value = "select f " +
            "from Follow f " +
            "inner join fetch f.following " +
            "where f.follower = :follower",
            countQuery = "select count(f) from Follow f " +
                    "where f.follower = :follower")
    Page<Follow> findByFollower(@Param("follower") User follower, Pageable pageable);

    @Query(value = "select f " +
            "from Follow f " +
            "inner join fetch f.follower " +
            "where f.following = :following",
            countQuery = "select count(f) from Follow f " +
                    "where f.following = :following")
    Page<Follow> findByFollowing(@Param("following") User following, Pageable pageable);
}
