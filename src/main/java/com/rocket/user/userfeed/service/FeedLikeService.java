package com.rocket.user.userfeed.service;

import static com.rocket.error.type.UserFeedErrorCode.FEED_ALREADY_FEED_LIKE;
import static com.rocket.error.type.UserFeedErrorCode.FEED_ALREADY_FEED_LIKE_CANCEL;
import static com.rocket.error.type.UserFeedErrorCode.FEED_LIKE_FAIL;

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

        FeedLike newFeedLike = feedLikeRepository.findByUserIdAndFeedId(user.getId(),
            feedId).orElse(null);

        if (newFeedLike == null) {
            try {
                newFeedLike = feedLikeRepository.save(FeedLike.builder()
                    .feed(feedService.getFeed(feedId))
                    .user(user)
                    .build());
            } catch (Exception e) {
                throw new UserFeedException(FEED_LIKE_FAIL);
            }
        } else {
            throw new UserFeedException(FEED_ALREADY_FEED_LIKE);
        }
        return newFeedLike;
    }

    @Transactional
    public void deleteFeedLike(User user, Long feedId) {
        FeedLike feedLike = feedLikeRepository.findByUserIdAndFeedId(user.getId(),
            feedId).orElse(null);

        if (feedLike == null) {
            throw new UserFeedException(FEED_ALREADY_FEED_LIKE_CANCEL);
        }
        feedLikeRepository.delete(feedLike);
    }

    public Long getCount(Long feedId) {
        return feedLikeRepository.countByFeedId(feedId);
    }
}
