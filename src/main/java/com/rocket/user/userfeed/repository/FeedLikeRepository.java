package com.rocket.user.userfeed.repository;

import com.rocket.user.userfeed.entity.FeedLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedLikeRepository extends JpaRepository<FeedLike, Long> {
}
