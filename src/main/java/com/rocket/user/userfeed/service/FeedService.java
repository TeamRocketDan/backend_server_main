package com.rocket.user.userfeed.service;

import static com.rocket.error.type.UserErrorCode.USER_DELETED_AT;
import static com.rocket.error.type.UserErrorCode.USER_NOT_FOUND;
import static com.rocket.error.type.UserFeedErrorCode.FEED_NOT_FOUND;

import com.rocket.error.exception.UserException;
import com.rocket.error.exception.UserFeedException;
import com.rocket.user.user.entity.User;
import com.rocket.user.user.repository.UserRepository;
import com.rocket.user.userfeed.dto.FeedDto;
import com.rocket.user.userfeed.dto.FeedSearchCondition;
import com.rocket.user.userfeed.entity.Feed;
import com.rocket.user.userfeed.repository.FeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getUser(String uuid) {
        User user = userRepository.findByUuid(uuid)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        if (user.getDeletedAt() != null) {
            throw new UserException(USER_DELETED_AT);
        }
        return user;
    }

    public Feed getFeed(Long feedId) {
        return feedRepository.findById(feedId).orElse(null);
    }

    public Page<Feed> getFeeds(User user, FeedSearchCondition searchCondition,
        Pageable pageable) {

        return feedRepository.findByUserIdAndRcate1EqualsAndRcate2EqualsOrderByCreatedAtDesc(
            user.getId(),
            searchCondition.getRcate1()
            , searchCondition.getRcate2()
            , PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
    }

    @Transactional
    public FeedDto createFeed(User user, FeedDto feedDto) {

        Feed feed = feedRepository.save(Feed.builder()
            .user(user)
            .title(feedDto.getTitle())
            .content(feedDto.getContent())
            .rcate1(feedDto.getRcate1())
            .rcate2(feedDto.getRcate2())
            .rcate3(feedDto.getRcate3())
            .longitude(feedDto.getLongitude())
            .latitude(feedDto.getLatitude())
            .build());
        log.info("feed {}", feed);

        return new ModelMapper().map(feed, FeedDto.class);
    }

    @Transactional
    public FeedDto updateFeed(Long id, FeedDto feedDto) {

        Feed feed = feedRepository.findById(id)
            .orElseThrow(() -> new UserFeedException(FEED_NOT_FOUND));
        feed.updateFeed(feedDto);

        return new ModelMapper().map(feed, FeedDto.class);
    }

    @Transactional
    public void deleteFeed(Long userId, Long FeedId) {

        Feed feed = feedRepository.findById(FeedId)
            .orElseThrow(() -> new UserFeedException(FEED_NOT_FOUND));

        try {
            feedRepository.delete(feed);
        } catch (Exception e) {
            log.error("[FeedService.deleteFeed] ERROR {}", e.getMessage());

            // TODO: 먼가 만들자
            throw new UserFeedException();
        }
    }
}
