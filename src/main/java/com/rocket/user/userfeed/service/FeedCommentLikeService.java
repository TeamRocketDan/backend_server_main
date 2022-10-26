package com.rocket.user.userfeed.service;

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
        return feedCommentLikeRepository.save(FeedCommentLike.builder()
            .user(user)
            .feedComment(feedComment)
            .build());
    }

    public FeedCommentLike getFeedCommentLike(Long feedCommentId, Long userId) {
        return feedCommentLikeRepository.findByFeedCommentIdAndUserId(feedCommentId, userId)
            .orElse(null);
    }

    public Long getCount(Long feedCommentId) {
        return feedCommentLikeRepository.countByFeedCommentId(feedCommentId);
    }

    @Transactional
    public void deleteFeedCommentLike(FeedCommentLike feedCommentLike) {
        feedCommentLikeRepository.delete(feedCommentLike);
    }
}
