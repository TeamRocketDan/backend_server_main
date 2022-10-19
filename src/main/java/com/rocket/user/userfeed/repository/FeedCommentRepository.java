package com.rocket.user.userfeed.repository;

import com.rocket.user.userfeed.entity.FeedComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedCommentRepository extends JpaRepository<FeedComment, Long> {
}
