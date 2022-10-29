package com.rocket.user.userfeed.service;

import com.rocket.user.user.entity.User;
import com.rocket.user.userfeed.dto.FeedCommentDto;
import com.rocket.user.userfeed.entity.Feed;
import com.rocket.user.userfeed.entity.FeedComment;
import com.rocket.user.userfeed.entity.FeedImage;
import com.rocket.user.userfeed.repository.FeedCommentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

    public Page<FeedComment> getFeedComments(Long feedId, Pageable pageable) {
        return feedCommentRepository.findByFeedId(feedId
            , PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
    }

    public FeedComment getFeedComment(Long id) {
        return feedCommentRepository.findById(id).orElse(null);
    }


    public Long getCount(Long feedId) {
        return feedCommentRepository.countByFeedId(feedId);
    }

    @Transactional
    public void deleteFeedComment(FeedComment feedComment) {
        feedCommentRepository.delete(feedComment);
    }
}
