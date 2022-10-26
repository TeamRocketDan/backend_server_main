package com.rocket.user.userfeed.repository;

import com.rocket.user.userfeed.entity.FeedLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface FeedLikeRepository extends JpaRepository<FeedLike, Long> {

    Optional<FeedLike> findByUserIdAndFeedId(Long userId, Long feedId);

    Long countByFeedId(Long feedId);
}
