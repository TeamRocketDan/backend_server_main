package com.rocket.user.userfeed.service;

import static com.rocket.error.type.UserFeedErrorCode.FEED_ALREADY_FEED_LIKE;
import static com.rocket.error.type.UserFeedErrorCode.FEED_ALREADY_FEED_LIKE_CANCEL;

import com.rocket.error.exception.UserFeedException;
import com.rocket.user.user.entity.User;
import com.rocket.user.userfeed.entity.FeedLike;
import com.rocket.user.userfeed.repository.FeedLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class FeedLikeService {

    private final FeedService feedService;
    private final FeedLikeRepository feedLikeRepository;

    @Transactional
    public FeedLike saveFeedLike(User user, Long feedId) {

        FeedLike newFeedLike
            = feedLikeRepository.findByUserIdAndFeedId(user.getId(), feedId).orElse(null);

        if (ObjectUtils.isEmpty(newFeedLike)) {
            newFeedLike = feedLikeRepository.save(FeedLike.builder()
                .feed(feedService.getFeed(feedId))
                .user(user)
                .build());
        } else {
            throw new UserFeedException(FEED_ALREADY_FEED_LIKE);
        }
        return newFeedLike;
    }

    public boolean getFeedLike(User user, Long feedId) {

        boolean isFeedLike;

        if (feedLikeRepository.findByUserIdAndFeedId(user.getId(), feedId).isPresent()) {
            isFeedLike = true;
        } else {
            isFeedLike = false;
        }
        return isFeedLike;
    }

    @Transactional
    public void deleteFeedLike(User user, Long feedId) {

        FeedLike feedLike = feedLikeRepository.findByUserIdAndFeedId(user.getId(),
            feedId).orElse(null);

        if (ObjectUtils.isEmpty(feedLike)) {
            throw new UserFeedException(FEED_ALREADY_FEED_LIKE_CANCEL);
        }
        feedLikeRepository.delete(feedLike);
    }

}
