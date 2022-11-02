package com.rocket.user.userfeed.service;

import static com.rocket.error.type.UserFeedErrorCode.FEED_COMMENT_ALREADY_FEED_LIKE;
import static com.rocket.error.type.UserFeedErrorCode.FEED_COMMENT_ALREADY_FEED_LIKE_CANCEL;
import static com.rocket.error.type.UserFeedErrorCode.FEED_COMMENT_LIKE_FAIL;

import com.rocket.error.exception.UserFeedException;
import com.rocket.user.user.entity.User;
import com.rocket.user.userfeed.entity.FeedComment;
import com.rocket.user.userfeed.entity.FeedCommentLike;
import com.rocket.user.userfeed.repository.FeedCommentLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedCommentLikeService {

    private final FeedCommentLikeRepository feedCommentLikeRepository;

    @Transactional
    public FeedCommentLike saveFeedCommentLike(User user, FeedComment feedComment) {

        FeedCommentLike newFeedCommentLike
            = feedCommentLikeRepository.findByUserIdAndFeedCommentId(user.getId(),
            feedComment.getId()).orElse(null);

        if (newFeedCommentLike == null) {
            try {
                newFeedCommentLike = feedCommentLikeRepository.save(FeedCommentLike.builder()
                    .user(user)
                    .feedComment(feedComment)
                    .build());
            } catch (Exception e) {
                throw new UserFeedException(FEED_COMMENT_LIKE_FAIL);
            }
        } else {
            throw new UserFeedException(FEED_COMMENT_ALREADY_FEED_LIKE);
        }
        return newFeedCommentLike;
    }

    public FeedCommentLike getFeedCommentLike(Long userId, Long feedCommentId) {
        return feedCommentLikeRepository.findByUserIdAndFeedCommentId(userId, feedCommentId)
            .orElse(null);
    }

    public boolean isFeedCommentLike(User user, Long feedCommentId) {

        boolean isFeedCommentLike;
        if (feedCommentLikeRepository.findByUserIdAndFeedCommentId(user.getId(), feedCommentId)
            .isPresent()) {
            isFeedCommentLike = true;
        } else {
            isFeedCommentLike = false;
        }
        return isFeedCommentLike;
    }


    @Transactional
    public void deleteFeedCommentLike(User user, Long commentId) {

        FeedCommentLike feedCommentLike
            = feedCommentLikeRepository.findByUserIdAndFeedCommentId(user.getId(), commentId)
            .orElse(null);
        if (ObjectUtils.isEmpty(feedCommentLike)) {
            throw new UserFeedException(FEED_COMMENT_ALREADY_FEED_LIKE_CANCEL);
        }
        feedCommentLikeRepository.delete(feedCommentLike);
    }
}
