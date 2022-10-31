package com.rocket.user.userfeed.service;

import static com.rocket.error.type.UserFeedErrorCode.FEED_COMMENT_DELETE_FAIL;
import static com.rocket.error.type.UserFeedErrorCode.FEED_COMMENT_NOT_FOUND;
import static com.rocket.error.type.UserFeedErrorCode.FEED_COMMENT_UPDATE_FAIL;

import com.rocket.error.exception.UserFeedException;
import com.rocket.user.user.entity.User;
import com.rocket.user.userfeed.dto.FeedCommentDto;
import com.rocket.user.userfeed.entity.Feed;
import com.rocket.user.userfeed.entity.FeedComment;
import com.rocket.user.userfeed.repository.FeedCommentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedCommentService {

    private final FeedCommentRepository feedCommentRepository;

    @Transactional
    public FeedComment createFeedComment(User user, Feed feed, FeedCommentDto feedCommentDto) {
        return feedCommentRepository.save(FeedComment.builder()
            .user(user)
            .feed(feed)
            .comment(feedCommentDto.getComment())
            .build());
    }

    public FeedComment getFeedComment(Long id) {
        return feedCommentRepository.findById(id).orElse(null);
    }

    public Page<FeedComment> getFeedComments(Long feedId, Pageable pageable) {

        return feedCommentRepository.findByFeedId(feedId
            , PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
    }

    @Transactional
    public FeedCommentDto updateFeedComment(Long commentId, FeedCommentDto feedCommentDto) {

        FeedComment feedComment = feedCommentRepository.findById(commentId)
            .orElseThrow(() -> new UserFeedException(FEED_COMMENT_NOT_FOUND));

        try {
            feedComment.updateFeedComment(feedCommentDto);
        } catch (Exception e) {
            throw new UserFeedException(FEED_COMMENT_UPDATE_FAIL);
        }
        return new ModelMapper().map(feedComment, FeedCommentDto.class);
    }

    public Long getCount(Long feedId) {
        return feedCommentRepository.countByFeedId(feedId);
    }

    @Transactional
    public void deleteFeedComment(FeedComment feedComment) {

        try {
            feedCommentRepository.delete(feedComment);
        } catch (Exception e) {
            throw new UserFeedException(FEED_COMMENT_DELETE_FAIL);
        }
        feedCommentRepository.delete(feedComment);

    }
}
