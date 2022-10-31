package com.rocket.user.userfeed.controller;

import static com.rocket.error.type.AuthErrorCode.INVALID_ACCESS_TOKEN;
import static com.rocket.error.type.UserFeedErrorCode.FEED_COMMENT_USER_NOT_MATCH;

import com.rocket.common.response.PageResponse;
import com.rocket.config.jwt.AuthToken;
import com.rocket.config.jwt.AuthTokenProvider;
import com.rocket.error.exception.AuthException;
import com.rocket.error.exception.UserFeedException;
import com.rocket.error.type.UserFeedErrorCode;
import com.rocket.user.user.entity.User;
import com.rocket.user.userfeed.dto.FeedCommentDto;
import com.rocket.user.userfeed.dto.FeedDto;
import com.rocket.user.userfeed.dto.FeedSearchCondition;
import com.rocket.user.userfeed.entity.Feed;
import com.rocket.user.userfeed.entity.FeedComment;
import com.rocket.user.userfeed.entity.FeedCommentLike;
import com.rocket.user.userfeed.service.FeedCommentLikeService;
import com.rocket.user.userfeed.service.FeedCommentService;
import com.rocket.user.userfeed.service.FeedLikeService;
import com.rocket.user.userfeed.service.FeedService;
import com.rocket.user.userfeed.vo.FeedCommentResponse;
import com.rocket.user.userfeed.vo.FeedResponse;
import com.rocket.utils.ApiUtils;
import com.rocket.utils.ApiUtils.ApiResult;
import com.rocket.utils.HeaderUtil;
import io.jsonwebtoken.Claims;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

// TODO: 서비스에서 예외처리하기
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feeds")
public class FeedController {

    private final FeedService feedService;

    private final FeedLikeService feedLikeService;

    private final FeedCommentService feedCommentService;

    private final FeedCommentLikeService feedCommentLikeService;

    private final AuthTokenProvider tokenProvider;

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResult<FeedResponse> createFeed(HttpServletRequest request
        , @RequestPart("files") List<MultipartFile> multipartFiles
        , @RequestPart("feed") Feed feed) {
        User user = getUser(request);

        if (multipartFiles == null || multipartFiles.isEmpty()) {
            throw new UserFeedException(UserFeedErrorCode.UPLOAD_AT_LEAST_ONE_IMAGE);
        }

        FeedDto newFeed = feedService.createFeed(user, feed, multipartFiles);

        return ApiUtils.success(FeedResponse.builder()
            .feedId(String.valueOf(newFeed.getId()))
            .build());
    }

    @GetMapping("/{feedId}")
    public ApiResult<FeedResponse> getFeed(HttpServletRequest request
        , @PathVariable("feedId") String feedId) {
        User user = getUser(request);
        Feed feed = feedService.getFeed(Long.valueOf(feedId));

        if (!feed.getUser().getId().equals(user.getId())) {
            throw new UserFeedException(UserFeedErrorCode.FEED_USER_NOT_MATCH);
        }

        return ApiUtils.success(getFeedResponse(user, feed));
    }

    @GetMapping
    public ApiResult<PageResponse<FeedResponse>> getFeeds(HttpServletRequest request
        , FeedSearchCondition searchCondition
        , Pageable pageable) {

        User user = getUser(request);
        Page<Feed> feeds = feedService.getFeeds(user, searchCondition, pageable);
        List<FeedResponse> feedResponses = new ArrayList<>();

        for (Feed feed : feeds) {
            feedResponses.add(getFeedResponse(user, feed));
        }

        return ApiUtils.success(PageResponse.<FeedResponse>builder()
            .lastPage(feeds.isLast())
            .firstPage(feeds.isFirst())
            .totalPages(feeds.getTotalPages())
            .totalElements(feeds.getTotalElements())
            .size(pageable.getPageSize())
            .currentPage(pageable.getPageNumber())
            .content(feedResponses)
            .build());
    }

    @GetMapping("/feedList")
    public ApiResult<PageResponse<FeedResponse>> getFeedList(FeedSearchCondition searchCondition
        , Pageable pageable) {

        Page<Feed> feeds = feedService.getFeedList(searchCondition, pageable);
        List<FeedResponse> feedResponses = new ArrayList<>();

        for (Feed feed : feeds) {
            feedResponses.add(getFeedListResponse(feed));
        }

        return ApiUtils.success(PageResponse.<FeedResponse>builder()
            .lastPage(feeds.isLast())
            .firstPage(feeds.isFirst())
            .totalPages(feeds.getTotalPages())
            .totalElements(feeds.getTotalElements())
            .size(pageable.getPageSize())
            .currentPage(pageable.getPageNumber())
            .content(feedResponses)
            .build());
    }

    @PatchMapping(value = "/{feedId}")
    public ApiResult<FeedResponse> updateFeed(HttpServletRequest request
        , @PathVariable("feedId") String feedId
        , @RequestPart("feed") FeedDto updateFeed) {

        User user = getUser(request);
        Feed feed = feedService.getFeed(Long.valueOf(feedId));

        if (!feed.getUser().getId().equals(user.getId())) {
            throw new UserFeedException(UserFeedErrorCode.FEED_USER_NOT_MATCH);
        } else {
            feedService.updateFeed(feed.getId(), updateFeed);
        }

        return ApiUtils.success(FeedResponse.builder()
            .feedId(String.valueOf(feed.getId()))
            .title(feed.getTitle())
            .content(feed.getContent())
            .rcate1(feed.getRcate1())
            .rcate2(feed.getRcate2())
            .longitude(feed.getLongitude())
            .latitude(feed.getLatitude())
            .build());
    }

    @DeleteMapping("/{feedId}")
    public ApiResult deleteFeed(HttpServletRequest request
        , @PathVariable("feedId") String feedId) {
        User user = getUser(request);

        feedService.deleteFeed(user.getId(), Long.valueOf(feedId));

        return ApiUtils.success(null);
    }

    @PostMapping("/{feedId}/like")
    public ApiResult saveFeedLike(HttpServletRequest request
        , @PathVariable("feedId") String feedId) {
        User user = getUser(request);
        feedLikeService.saveFeedLike(user, Long.valueOf(feedId));

        return ApiUtils.success(null);
    }

    @DeleteMapping("/{feedId}/like")
    public ApiResult deleteFeedLike(HttpServletRequest request
        , @PathVariable("feedId") String feedId) {
        User user = getUser(request);
        feedLikeService.deleteFeedLike(user, Long.valueOf(feedId));

        return ApiUtils.success(null);
    }

    @PostMapping("/{feedId}/comments")
    public ApiResult<FeedCommentResponse> createFeedComment(
        HttpServletRequest request
        , @PathVariable("feedId") String feedId
        , @RequestPart("feedComment") FeedCommentDto feedCommentDto) {

        User user = getUser(request);
        Feed feed = feedService.getFeed(Long.valueOf(feedId));

        if (feed == null) {
            throw new UserFeedException(UserFeedErrorCode.FEED_NOT_FOUND);
        }

        FeedComment feedComment = feedCommentService.createFeedComment(user, feed, feedCommentDto);

        return ApiUtils.success(FeedCommentResponse.builder()
            .feedCommentId(String.valueOf(feedComment.getId()))
            .comment(feedComment.getComment())
            .build());
    }

    @GetMapping("/{feedId}/comments")
    public ApiResult<PageResponse<FeedResponse>> getFeedComments(HttpServletRequest request
        , @PathVariable("feedId") String feedId
        , Pageable pageable) {
        User user = getUser(request);
        Feed feed = feedService.getFeed(Long.valueOf(feedId));

        if (feed == null) {
            throw new UserFeedException(UserFeedErrorCode.FEED_NOT_FOUND);
        }

        Page<FeedComment> feedComments = feedCommentService.getFeedComments(
            Long.valueOf(feedId), pageable);

        List<FeedResponse> content = new ArrayList<>();

        for (FeedComment feedComment : feedComments) {
            content.add(FeedResponse.builder()
                .userId(String.valueOf(user.getId()))
                .profileImagePath(user.getProfileImage())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .content(feedComment.getComment())
                .commentLikeCnt((long) feedComment.getFeedCommentLike().size())

                .build());
        }

        return ApiUtils.success(PageResponse.<FeedResponse>builder()
            .lastPage(feedComments.isLast())
            .firstPage(feedComments.isFirst())
            .totalPages(feedComments.getTotalPages())
            .totalElements(feedComments.getTotalElements())
            .size(pageable.getPageSize())
            .currentPage(pageable.getPageNumber())
            .content(content)
            .build());
    }

    @PatchMapping(value = "/{feedId}/comments/{commentId}")
    public ApiResult<FeedCommentResponse> updateFeedComment(HttpServletRequest request
        , @PathVariable("feedId") String feedId
        , @PathVariable("commentId") String commentId
        , @RequestPart("feedComment") FeedCommentDto updateFeedComment) {

        User user = getUser(request);
        FeedComment feedComment = feedCommentService.getFeedComment(Long.valueOf(commentId));

        if (!feedComment.getUser().getId().equals(user.getId())) {
            throw new UserFeedException(UserFeedErrorCode.FEED_USER_NOT_MATCH);
        } else {
            feedCommentService.updateFeedComment(Long.valueOf(commentId), updateFeedComment);
        }

        return ApiUtils.success(FeedCommentResponse.builder()
            .comment(updateFeedComment.getComment())
            .build());
    }

    @DeleteMapping("/{feedId}/comments/{commentId}")
    public ApiResult<FeedCommentDto> deleteFeedComment(HttpServletRequest request
        , @PathVariable("feedId") String feedId
        , @PathVariable("commentId") String commentId) {

        User user = getUser(request);

        FeedComment feedComment
            = feedCommentService.getFeedComment(Long.valueOf(commentId));

        if (!user.getId().equals(feedComment.getUser().getId())) {
            throw new UserFeedException(FEED_COMMENT_USER_NOT_MATCH);
        }

        feedCommentService.deleteFeedComment(feedComment);

        return ApiUtils.success(FeedCommentDto.builder()
            .feedCommentId(feedComment.getId())
            .comment(feedComment.getComment())
            .build());
    }

    @PostMapping("/{feedId}/comments/{commentId}/like")
    public ApiResult saveFeedCommentLike(HttpServletRequest request
        , @PathVariable("feedId") String feedId
        , @PathVariable("commentId") String commentId) {
        User user = getUser(request);
        FeedComment feedComment = feedCommentService.getFeedComment(Long.valueOf(commentId));

        feedCommentLikeService.saveFeedCommentLike(user, feedComment);

        // TODO: 실패 케이스에 대해서 구현 필요
        return ApiUtils.success(null);
    }

    @DeleteMapping("/{feedId}/comments/{commentId}/like")
    public ApiResult deleteFeedCommentLike(HttpServletRequest request
        , @PathVariable("feedId") String feedId
        , @PathVariable("commentId") String commentId) {
        User user = getUser(request);
        FeedCommentLike feedCommentLike
            = feedCommentLikeService.getFeedCommentLike(Long.valueOf(commentId),
            Long.valueOf(user.getId()));

        if (feedCommentLike == null) {
            //TODO: 에러 처리
        }

        feedCommentLikeService.deleteFeedCommentLike(feedCommentLike);

        return ApiUtils.success(null);
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
            .longitude(feed.getLongitude())
            .latitude(feed.getLatitude())

            .feedLikeCnt((long) feed.getFeedLike().size())
            .feedCommentCnt((long) feed.getFeedComment().size())
            .feedImages(feed.getFeedImage().stream()
                .map(feedImage -> feedImage.getImagePaths()).collect(Collectors.toList()))
            .build();
//            .feedLikeCnt(feedLikeService.getCount(feed.getId())) // TODO: null 예외 처리 필요
//            .feedCommentCnt(feedCommentService.getCount(feed.getId()))
//            .feedImages(feedImageService.getFeedImages(feed.getId()))
//            .comments(feedCommentService.getFeedComments(feed.getId(), Pageable.ofSize(10)))
//            .commentLikeCnt(feedCommentLikeService.getCount(feed.getId()))
    }

    private FeedResponse getFeedListResponse(Feed feed) {
        return FeedResponse.builder()
            .title(feed.getTitle())
            .content(feed.getContent())
            .rcate1(feed.getRcate1())
            .rcate2(feed.getRcate2())
            .longitude(feed.getLongitude())
            .latitude(feed.getLatitude())

            .feedLikeCnt((long) feed.getFeedLike().size())
            .feedCommentCnt((long) feed.getFeedComment().size())
            .feedImages(feed.getFeedImage().stream()
                .map(feedImage -> feedImage.getImagePaths()).collect(Collectors.toList()))
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

        return feedService.getUser(authToken.getUuid(request));
    }
}