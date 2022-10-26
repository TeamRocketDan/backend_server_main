package com.rocket.user.userfeed.repository;

import com.rocket.user.user.entity.User;
import com.rocket.user.userfeed.entity.Feed;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface FeedRepository extends JpaRepository<Feed, Long> {

    Page<Feed> findByUserIdAndRcate1EqualsAndRcate2EqualsOrderByCreatedAtDesc(Long userId
        , String rcate1
        , String rcate2
        , PageRequest pageRequest);

}
