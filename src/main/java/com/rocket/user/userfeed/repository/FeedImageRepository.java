package com.rocket.user.userfeed.repository;

import com.rocket.user.userfeed.entity.FeedImage;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface FeedImageRepository extends JpaRepository<FeedImage, Long> {

    Optional<FeedImage> findByFeedId(Long feedId);
}
