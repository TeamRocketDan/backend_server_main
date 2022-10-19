package com.rocket.user.userfeed.repository;

import com.rocket.user.userfeed.entity.FeedCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedCommentLikeRepository extends JpaRepository<FeedCommentLike, Long> {
}
