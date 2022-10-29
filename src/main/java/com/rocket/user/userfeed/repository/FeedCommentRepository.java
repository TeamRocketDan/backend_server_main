package com.rocket.user.userfeed.repository;

import com.rocket.user.userfeed.entity.FeedComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface FeedCommentRepository extends JpaRepository<FeedComment, Long> {

    Page<FeedComment> findByFeedId(Long feedId, PageRequest pageRequest);


    Long countByFeedId(Long feedId);
}
