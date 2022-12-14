package com.rocket.user.userfeed.service;

import com.rocket.error.exception.UserException;
import com.rocket.error.exception.UserFeedException;
import com.rocket.error.type.UserFeedErrorCode;
import com.rocket.user.user.entity.User;
import com.rocket.user.user.repository.UserRepository;
import com.rocket.user.userfeed.dto.FeedDto;
import com.rocket.user.userfeed.dto.FeedListDto;
import com.rocket.user.userfeed.dto.FeedSearchCondition;
import com.rocket.user.userfeed.entity.Feed;
import com.rocket.user.userfeed.entity.FeedImage;
import com.rocket.user.userfeed.repository.FeedRepository;
import com.rocket.user.userfeed.repository.query.FeedQueryRepository;
import com.rocket.utils.PagingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.rocket.error.type.UserErrorCode.USER_DELETED_AT;
import static com.rocket.error.type.UserErrorCode.USER_NOT_FOUND;
import static com.rocket.error.type.UserFeedErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;
    private final UserRepository userRepository;
    private final FeedImageService feedImageService;
    private final FeedQueryRepository feedQueryRepository;

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

        Feed getFeed = feedRepository.findById(feedId).orElse(null);

        if (getFeed == null) {
            throw new UserFeedException(UserFeedErrorCode.FEED_NOT_FOUND);
        }
        return getFeed;
    }

    public Page<Feed> getFeedList(User user, FeedSearchCondition searchCondition,
                                  Pageable pageable) {

        return feedRepository.findByUserIdAndRcate1EqualsAndRcate2EqualsOrderByCreatedAtDesc(
            user.getId(),
            searchCondition.getRcate1()
            , searchCondition.getRcate2()
            , PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
    }

    public PagingResponse<FeedListDto> getFeedListModify(
            User user,
            FeedSearchCondition searchCondition,
            Pageable pageable,
            boolean isMain) {

        Page<FeedListDto> feeds = feedQueryRepository.findByRcate1EqualsAndRcate2EqualsOrderByCreatedAtDesc(
                searchCondition,
                user,
                pageable,
                isMain
        );

        List<Long> feedsId = feeds.stream()
                .map(FeedListDto::getFeedId).collect(Collectors.toList());
        List<FeedImage> feedImages = feedQueryRepository.findByAllFeedIn(feedsId);

        mergeFeedList(feedImages, feeds);

        return PagingResponse.fromEntity(feeds);
    }



    public Page<Feed> getFeeds(FeedSearchCondition searchCondition,
        Pageable pageable) {

        return feedRepository.findByRcate1EqualsAndRcate2EqualsOrderByCreatedAtDesc(
            searchCondition.getRcate1()
            , searchCondition.getRcate2()
            , PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
    }

    @Transactional
    public FeedDto createFeed(User user, Feed feed, List<MultipartFile> multipartFiles) {

        Feed newFeed = null;

        if (multipartFiles.size() > 4) {
            throw new UserFeedException(FEED_IMAGE_UPLOAD_COUNT_OVER);
        }

        try {
            newFeed = feedRepository.save(Feed.builder()
                .user(user)
                .title(feed.getTitle())
                .content(feed.getContent())
                .rcate1(feed.getRcate1())
                .rcate2(feed.getRcate2())
                .longitude(feed.getLongitude())
                .latitude(feed.getLatitude())
                .build());

            feedImageService.createFeedImage(user, newFeed, multipartFiles);

        } catch (Exception e) {
            log.error("[FeedService.createFeed] ERROR {}", e);
            throw new UserFeedException(UserFeedErrorCode.FEED_CREATE_FAIL);
        }
        return new ModelMapper().map(newFeed, FeedDto.class);

    }

    @Transactional
    public FeedDto updateFeed(Long id, FeedDto feedDto) {

        Feed feed = feedRepository.findById(id)
            .orElseThrow(() -> new UserFeedException(FEED_NOT_FOUND));

        try {
            feed.updateFeed(feedDto);
        } catch (Exception e) {
            log.error("[FeedService.updateFeed] ERROR {}", e.getMessage());
            throw new UserFeedException(FEED_UPDATE_FAIL);
        }

        return new ModelMapper().map(feed, FeedDto.class);
    }

    @Transactional
    public void deleteFeed(Long userId, Long feedId) {

        Feed feed = feedRepository.findById(feedId)
            .orElseThrow(() -> new UserFeedException(FEED_NOT_FOUND));

        if (feed.getUser().getId().equals(userId)) {
            feedRepository.delete(feed);
        } else {
            throw new UserFeedException(FEED_USER_NOT_MATCH);
        }
    }

    private void mergeFeedList(List<FeedImage> feedImages, Page<FeedListDto> feedList) {
        for (int i = 0; i < feedList.getContent().size(); i++) {
            for (int j = 0; j < feedImages.size(); j++) {
                if (Objects.equals(
                        feedList.getContent().get(i).getFeedId(),
                        feedImages.get(j).getFeed().getId())) {
                    feedList.getContent().get(i).addImage(feedImages.get(j).getImagePaths());
                }
            }
        }
    }
}