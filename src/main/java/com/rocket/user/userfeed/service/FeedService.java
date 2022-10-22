package com.rocket.user.userfeed.service;

import static com.rocket.error.type.UserFeedErrorCode.FEED_NOT_FOUND;

import com.rocket.error.exception.UserFeedException;
import com.rocket.user.user.entity.User;
import com.rocket.user.userfeed.dto.FeedSearchCondition;
import com.rocket.user.userfeed.entity.Feed;
import com.rocket.user.userfeed.dto.FeedDto;
import com.rocket.user.userfeed.dto.FeedForm;
import com.rocket.user.userfeed.repository.FeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository;

    public Feed getFeed(Long feedId) {
        return feedRepository.findById(feedId).orElse(null);
    }

    public Page<Feed> getFeeds(User user, FeedSearchCondition searchCondition) {
        return feedRepository.findByUserIdAndRcate1EqualsAndRcate2EqualsOrderByCreatedAtDesc(String.valueOf(user.getId()),
            searchCondition.getRcate1()
            , searchCondition.getRcate2()
            , PageRequest.of(searchCondition.getPage(), searchCondition.getSize()));
    }

    @Transactional
    public Feed createFeed(User user, FeedDto feedDto) {
        return feedRepository.save(Feed.builder()
            .user(user)
            .title(feedDto.getTitle())
            .content(feedDto.getContent())
            .rcate1(feedDto.getRcate1())
            .rcate2(feedDto.getRcate2())
            .rcate3(feedDto.getRcate3())
            .longitude(feedDto.getLongitude())
            .latitude(feedDto.getLatitude())
            .build());
    }

    @Transactional
    public Feed updateFeed(User user, Feed feed, FeedForm feedForm) {
        return feedRepository.save(Feed.builder()
            .title(feedForm.getTitle())
            .content(feedForm.getContent())
            .rcate1(feedForm.getRcate1())
            .rcate2(feedForm.getRcate2())
            .rcate3(feedForm.getRcate3())
            .longitude(feedForm.getLongitude())
            .latitude(feedForm.getLatitude())
            .build());
    }

    @Transactional
    public void deleteFeed(Long userId, Long FeedId) {
        Feed feed = feedRepository.findById(userId)
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
