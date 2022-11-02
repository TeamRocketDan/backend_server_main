package com.rocket.user.userfeed.repository;

import com.rocket.user.userfeed.entity.FeedCommentLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface FeedCommentLikeRepository extends JpaRepository<FeedCommentLike, Long> {

    Optional<FeedCommentLike> findByUserIdAndFeedCommentId(Long userId, Long feedCommentId);

    Long countByFeedCommentId(Long feedCommentId);
}
