package com.rocket.user.userfeed.repository;

import com.rocket.user.userfeed.entity.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Long> {
}
