package com.rocket.user.userfeed.controller;

import static com.rocket.error.type.AuthErrorCode.INVALID_ACCESS_TOKEN;

import com.rocket.config.jwt.AuthToken;
import com.rocket.config.jwt.AuthTokenProvider;
import com.rocket.error.exception.AuthException;
import com.rocket.error.exception.UserFeedException;
import com.rocket.error.type.UserFeedErrorCode;
import com.rocket.user.user.entity.User;
import com.rocket.user.user.service.UserService;
import com.rocket.user.userfeed.dto.FeedSearchCondition;
import com.rocket.user.userfeed.entity.Feed;
import com.rocket.user.userfeed.dto.FeedDto;
import com.rocket.user.userfeed.entity.FeedImage;
import com.rocket.user.userfeed.service.FeedImageService;
import com.rocket.user.userfeed.service.FeedService;
import com.rocket.user.userfeed.vo.FeedResponse;
import com.rocket.user.userfeed.vo.FeedResponse.FeedResponseBuilder;
import com.rocket.user.userfeed.vo.PageResponse;
import com.rocket.utils.ApiUtils;
import com.rocket.utils.ApiUtils.ApiResult;
import com.rocket.utils.HeaderUtil;
import io.jsonwebtoken.Claims;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feeds")
public class FeedController {

    private final UserService userService;

    private final FeedService feedService;

    private final AuthTokenProvider tokenProvider;

    private final FeedImageService feedImageService;

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResult<FeedResponse>> createFeed(HttpServletRequest request
        , @RequestPart("files") MultipartFile[] files
        , @RequestPart("feed") FeedDto feedDto) {
        User user = getUser(request);
        Feed feed = feedService.createFeed(user, feedDto);
        feedImageService.createFeedImage(feed, files);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiUtils.success(FeedResponse.builder()
                .feedId(String.valueOf(feed.getId()))
                .build()));
    }

    @GetMapping
    public ResponseEntity<ApiResult<PageResponse>> getFeeds(HttpServletRequest request
        , @RequestParam FeedSearchCondition searchCondition) {
        User user = getUser(request);
        Page<Feed> feeds = feedService.getFeeds(user, searchCondition);

        List<FeedResponse> feedResponses = new ArrayList<>();

        for (Feed feed : feeds) {
            feedResponses.add(getFeedResponse(user, feed));
        }

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiUtils.success(PageResponse.builder()
                    .lastPage(feeds.isLast())
                    .firstPage(feeds.isFirst())
                    .totalPages(feeds.getTotalPages())
                    .totalElements(feeds.getTotalElements())
                    .size(searchCondition.getSize())
                    .currentPage(searchCondition.getPage())
                    .content(feedResponses)
                    .build()));
    }

    @PatchMapping(value = "{feedId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResult<FeedResponse>> updateImagePaths(HttpServletRequest request
        , @PathVariable("feedId") String feedId
        , @RequestPart("files") MultipartFile[] files) {
        User user = getUser(request);
        FeedImage feedImage = feedImageService.getFeedImage(Long.valueOf(feedId));
        // TODO: feedImageService에서 S3 imagePath 수정하는 코드 구현 필요

        // TODO: feedImageService에서 불러온 feedImage에서 feedId를 꺼내서 해당 id를 기반으로 Feed도 불러옴
        Feed feed = feedService.getFeed(Long.valueOf(feedId));

        if (feed == null) {
            // TODO: UserFeedException 처리
            throw new UserFeedException(UserFeedErrorCode.FEED_NOT_FOUND);
        }

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiUtils.success(FeedResponse.builder()
                // .feedId()
                // .title()
                // .content()
                // .imagePaths()
                .build()));
    }

    @DeleteMapping("{feedId}")
    public ResponseEntity deleteFeed(HttpServletRequest request
        , @PathVariable("feedId") String feedId) {
        User user = getUser(request);
        feedService.deleteFeed(Long.valueOf(user.getId()), Long.valueOf(feedId));

        return ResponseEntity.status(HttpStatus.OK)
            .body(null);
    }

    private FeedResponse getFeedResponse(User user, Feed feed) {
        return FeedResponse.builder()
            .userId(String.valueOf(user.getId()))
            .feedId(String.valueOf(feed.getId()))
            .profileImagePath(user.getProfileImage())
            .nickname(user.getNickname())
            .email(user.getEmail())
            .title(feed.getTitle())
            .content(feed.getContent())
            .rcate1(feed.getRcate1())
            .rcate2(feed.getRcate2())
            .rcate3(feed.getRcate3())
            .longitude(feed.getLongitude())
            .latitude(feed.getLatitude())
            // .feedLikeCnt(0L)
            // .feedCommentCnt(0L)
            // .imagePaths(new ArrayList<>()) TODO: 추가 구현 필요
            .build();
    }

    private User getUser(HttpServletRequest request) {
        String accessToken = HeaderUtil.getAccessToken(request);
        AuthToken authToken = tokenProvider.convertAuthToken(accessToken);

        if (!authToken.validate(request)) {
            throw new AuthException(INVALID_ACCESS_TOKEN);
        }

        Claims claims = authToken.getTokenClaims(request);

        if (claims == null) {
            throw new AuthException(INVALID_ACCESS_TOKEN);
        }

        return userService.getUser(authToken.getUuid(request));
    }
}
