package com.rocket.user.userfeed.controller;

import static com.rocket.error.type.AuthErrorCode.INVALID_ACCESS_TOKEN;

import com.rocket.common.response.PageResponse;
import com.rocket.config.jwt.AuthToken;
import com.rocket.config.jwt.AuthTokenProvider;
import com.rocket.error.exception.AuthException;
import com.rocket.error.exception.UserFeedException;
import com.rocket.error.type.UserFeedErrorCode;
import com.rocket.user.user.entity.User;
import com.rocket.user.userfeed.dto.FeedCommentDto;
import com.rocket.user.userfeed.dto.FeedSearchCondition;
import com.rocket.user.userfeed.entity.Feed;
import com.rocket.user.userfeed.entity.FeedComment;
import com.rocket.user.userfeed.entity.FeedCommentLike;
import com.rocket.user.userfeed.entity.FeedImage;
import com.rocket.user.userfeed.service.FeedCommentLikeService;
import com.rocket.user.userfeed.service.FeedCommentService;
import com.rocket.user.userfeed.service.FeedImageService;
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
import org.springframework.web.bind.annotation.RequestBody;
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

    private final FeedImageService feedImageService;


    /**
     * front에서 보내는 방법 참고: https://jaimemin.tistory.com/2072
     */
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResult<FeedResponse> createFeed(HttpServletRequest request
        , @RequestPart("files") List<MultipartFile> multipartFiles
        , @RequestPart("feed") Feed feed) {

        if (multipartFiles == null
            || multipartFiles.isEmpty()) {
            throw new UserFeedException(UserFeedErrorCode.UPLOAD_AT_LEAST_ONE_IMAGE);
        }

        User user = getUser(request);
        feedService.createFeed(user, feed, multipartFiles);
        feedImageService.createFeedImage(user, feed, multipartFiles);

        return ApiUtils.success(FeedResponse.builder()
            .feedId(String.valueOf(feed.getId()))
            .build());
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
    public ApiResult<PageResponse<FeedResponse>> getFeedList(HttpServletRequest request
        , FeedSearchCondition searchCondition
        , Pageable pageable) {

        User user = getUser(request);
        Page<Feed> feeds = feedService.getFeedList(searchCondition, pageable);
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

    @GetMapping("/{feedId}")
    public ApiResult<FeedResponse> getFeed(HttpServletRequest request
        , @PathVariable("feedId") String feedId) {
        User user = getUser(request);
        Feed feed = feedService.getFeed(Long.valueOf(feedId));

        if (feed == null) {
            throw new UserFeedException(UserFeedErrorCode.FEED_NOT_FOUND);
        }

        return ApiUtils.success(getFeedResponse(user, feed));
    }

    @PatchMapping(value = "/{feedId}", consumes = {MediaType.APPLICATION_JSON_VALUE,
        MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResult<FeedResponse> updateImagePaths(HttpServletRequest request
        , @PathVariable("feedId") String feedId
        , @RequestPart("files") List<MultipartFile> multipartFiles) {

        // TODO: 싹 다 삭제 -> 싹 다 업로드
        User user = getUser(request);
        FeedImage feedImage = feedImageService.getFeedImage(Long.valueOf(feedId));
        // TODO: feedImageService에서 S3 imagePath 수정하는 코드 구현 필요

        // TODO: feedImageService에서 불러온 feedImage에서 feedId를 꺼내서 해당 id를 기반으로 Feed도 불러옴
        Feed feed = feedService.getFeed(Long.valueOf(feedId));

        if (feed == null) {
            // TODO: UserFeedException 처리
            throw new UserFeedException(UserFeedErrorCode.FEED_NOT_FOUND);
        }

        return ApiUtils.success(FeedResponse.builder()
//             .feedId()
//             .title()
//             .content()
//             .imagePaths()
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
    public ApiResult addFeedLike(HttpServletRequest request
        , @PathVariable("feedId") String feedId) {
        User user = getUser(request);
        feedLikeService.createFeedLike(user, Long.valueOf(feedId));

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
        , @RequestBody FeedCommentDto feedCommentDto) {
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
            user.getId(), Long.valueOf(feedId), pageable);

        List<FeedResponse> content = new ArrayList<>();

        for (FeedComment feedComment : feedComments) {
            content.add(FeedResponse.builder()
                .userId(String.valueOf(user.getId()))
                .profileImagePath(user.getProfileImage())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .comment(feedComment.getComment())
                .commentLikeCnt(
                    feedCommentLikeService.getCount(feedComment.getId()))
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

    @DeleteMapping("/{feedId}/comments/{commentId}")
    public ApiResult<FeedCommentDto> deleteComment(@PathVariable("feedId") String feedId
        , @PathVariable("commentId") String commentId) {
        FeedComment feedComment
            = feedCommentService.getFeedComment(Long.valueOf(commentId));
        feedCommentService.deleteFeedComment(feedComment);

        // TODO: 실패 케이스에 대해서 구현 필요
        return ApiUtils.success(FeedCommentDto.builder()
            .feedCommentId(feedComment.getId())
            .comment(feedComment.getComment())
            .build());
    }

    @PostMapping("/{feedId}/comments/{commentId}/like")
    public ApiResult likeComment(HttpServletRequest request
        , @PathVariable("feedId") String feedId
        , @PathVariable("commentId") String commentId) {
        User user = getUser(request);
        FeedComment feedComment = feedCommentService.getFeedComment(Long.valueOf(commentId));
        feedCommentLikeService.saveFeedCommentLike(user, feedComment);

        // TODO: 실패 케이스에 대해서 구현 필요
        return ApiUtils.success(null);
    }

    @DeleteMapping("/{feedId}/comments/{commentId}/like")
    public ApiResult unlikeComment(HttpServletRequest request
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

            .feedLikeCnt(feedLikeService.getCount(feed.getId())) // TODO: null 예외 처리 필요
            .feedCommentCnt(feedCommentService.getCount(feed.getId()))
            .imagePaths(new ArrayList<>())
            .comment(feed.getContent())
            .commentLikeCnt(feedCommentLikeService.getCount(feed.getId()))
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