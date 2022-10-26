package com.rocket.user.userfeed.service;

import com.rocket.error.exception.UserFeedException;
import com.rocket.user.user.entity.User;
import com.rocket.user.userfeed.entity.FeedLike;
import com.rocket.user.userfeed.repository.FeedLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedLikeService {

    private final FeedService feedService;
    private final FeedLikeRepository feedLikeRepository;

    @Transactional
    public FeedLike createFeedLike(User user, Long feedId) {
        return feedLikeRepository.save(FeedLike.builder()
            .feed(feedService.getFeed(feedId))
            .user(user)
            .build());
    }

    @Transactional
    public void deleteFeedLike(User user, Long feedId) {
        FeedLike feedLike = feedLikeRepository.findByUserIdAndFeedId(user.getId(),
            feedId).orElse(null);

        if (feedLike == null) {
            // TODO: 예외처리
            throw new UserFeedException();
        }

        feedLikeRepository.delete(feedLike);
    }

    public Long getCount(Long feedId) {
        return feedLikeRepository.countByFeedId(feedId);
    }
}
