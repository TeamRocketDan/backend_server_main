package com.rocket.user.userfeed.service;

import static com.rocket.error.type.UserFeedErrorCode.FEED_COMMENT_ALREADY_FEED_LIKE;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedCommentLikeService {

    private final FeedCommentLikeRepository feedCommentLikeRepository;

    @Transactional
    public FeedCommentLike saveFeedCommentLike(User user, FeedComment feedComment) {

        FeedCommentLike newFeedCommentLike
            = feedCommentLikeRepository.findByFeedCommentIdAndUserId(feedComment.getId(),
            user.getId()).orElse(null);

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

    public FeedCommentLike getFeedCommentLike(Long feedCommentId, Long userId) {
        return feedCommentLikeRepository.findByFeedCommentIdAndUserId(feedCommentId, userId)
            .orElse(null);
    }


    @Transactional
    public void deleteFeedCommentLike(FeedCommentLike feedCommentLike) {
        feedCommentLikeRepository.delete(feedCommentLike);
    }
}
