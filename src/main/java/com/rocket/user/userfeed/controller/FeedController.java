package com.rocket.user.userfeed.controller;

import static com.rocket.error.type.UserFeedErrorCode.FEED_COMMENT_USER_NOT_MATCH;

import com.rocket.common.response.PageResponse;
import com.rocket.error.exception.UserFeedException;
import com.rocket.error.type.UserFeedErrorCode;
import com.rocket.user.user.entity.User;
import com.rocket.user.userfeed.dto.FeedCommentDto;
import com.rocket.user.userfeed.dto.FeedDto;
import com.rocket.user.userfeed.dto.FeedSearchCondition;
import com.rocket.user.userfeed.entity.Feed;
import com.rocket.user.userfeed.entity.FeedComment;
import com.rocket.user.userfeed.service.FeedCommentLikeService;
import com.rocket.user.userfeed.service.FeedCommentService;
import com.rocket.user.userfeed.service.FeedLikeService;
import com.rocket.user.userfeed.service.FeedService;
import com.rocket.user.userfeed.vo.FeedCommentResponse;
import com.rocket.user.userfeed.vo.FeedResponse;
import com.rocket.utils.ApiUtils;
import com.rocket.utils.ApiUtils.ApiResult;
import com.rocket.utils.CommonRequestContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feeds")
public class FeedController {

    private final FeedService feedService;

    private final FeedLikeService feedLikeService;

    private final FeedCommentService feedCommentService;

    private final FeedCommentLikeService feedCommentLikeService;

    private final CommonRequestContext commonRequestContext;


    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResult<FeedResponse> createFeed(
        @RequestPart("files") List<MultipartFile> multipartFiles
        , @RequestPart("feed") Feed feed) {

        User user = getUser();

        if (multipartFiles == null || multipartFiles.isEmpty()) {
            throw new UserFeedException(UserFeedErrorCode.UPLOAD_AT_LEAST_ONE_IMAGE);
        }

        FeedDto newFeed = feedService.createFeed(user, feed, multipartFiles);

        return ApiUtils.success(FeedResponse.builder()
            .feedId(String.valueOf(newFeed.getId()))
            .build());
    }

    @GetMapping("/{feedId}")
    public ApiResult<FeedResponse> getFeed(@PathVariable("feedId") String feedId) {
        User user = getUser();
        Feed feed = feedService.getFeed(Long.valueOf(feedId));
        return ApiUtils.success(getUserFeedResponse(user, feed));
    }

    @GetMapping("/feedList")
    public ApiResult<PageResponse<FeedResponse>> getMyFeedList(FeedSearchCondition searchCondition
        , Pageable pageable) {

        User user = getUser();
        Page<Feed> feeds = feedService.getFeedList(user, searchCondition, pageable);
        List<FeedResponse> feedResponses = new ArrayList<>();

        for (Feed feed : feeds) {
            feedResponses.add(getUserFeedResponse(user, feed));
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

    @GetMapping
    public ApiResult<PageResponse<FeedResponse>> getFeeds(FeedSearchCondition searchCondition
        , Pageable pageable) {

        User user = getUser();

        Page<Feed> feeds = feedService.getFeeds(searchCondition, pageable);
        List<FeedResponse> feedResponses = new ArrayList<>();

        if (ObjectUtils.isEmpty(user)) {
            for (Feed feed : feeds) {
                feedResponses.add(getFeedResponse(feed));
            }
        } else {
            for (Feed feed : feeds) {
                feedResponses.add(getUserFeedResponse(user, feed));
            }
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
    public ApiResult<FeedResponse> updateFeed(@PathVariable("feedId") String feedId
        , @RequestPart("feed") FeedDto updateFeed) {

        User user = getUser();
        Feed feed = feedService.getFeed(Long.valueOf(feedId));

        if (!feed.getUser().getId().equals(user.getId())) {
            throw new UserFeedException(UserFeedErrorCode.FEED_USER_NOT_MATCH);
        } else {
            feedService.updateFeed(feed.getId(), updateFeed);
        }

        return ApiUtils.success(FeedResponse.builder()
            .feedId(String.valueOf(feed.getId()))
            .build());
    }

    @DeleteMapping("/{feedId}")
    public ApiResult deleteFeed(@PathVariable("feedId") String feedId) {
        User user = getUser();
        feedService.deleteFeed(user.getId(), Long.valueOf(feedId));
        return ApiUtils.success(null);
    }

    @PostMapping("/{feedId}/like")
    public ApiResult saveFeedLike(@PathVariable("feedId") String feedId) {
        User user = getUser();
        feedLikeService.saveFeedLike(user, Long.valueOf(feedId));
        return ApiUtils.success(null);
    }

    @DeleteMapping("/{feedId}/like")
    public ApiResult deleteFeedLike(@PathVariable("feedId") String feedId) {
        User user = getUser();
        feedLikeService.deleteFeedLike(user, Long.valueOf(feedId));
        return ApiUtils.success(null);
    }

    @PostMapping("/{feedId}/comments")
    public ApiResult<FeedCommentResponse> createFeedComment(@PathVariable("feedId") String feedId
        , @RequestPart("feedComment") FeedCommentDto feedCommentDto) {

        User user = getUser();
        Feed feed = feedService.getFeed(Long.valueOf(feedId));

        if (feed == null) {
            throw new UserFeedException(UserFeedErrorCode.FEED_NOT_FOUND);
        }

        FeedComment feedComment = feedCommentService.createFeedComment(user, feed, feedCommentDto);

        return ApiUtils.success(FeedCommentResponse.builder()
            .userName(user.getUsername())
            .feedCommentId(String.valueOf(feedComment.getId()))
            .comment(feedComment.getComment())
            .build());
    }

    @GetMapping("/{feedId}/comments")
    public ApiResult<PageResponse<FeedCommentResponse>> getFeedComments(
        @PathVariable("feedId") String feedId, Pageable pageable) {

        User user = getUser();
        Feed feed = feedService.getFeed(Long.valueOf(feedId));

        if (feed == null) {
            throw new UserFeedException(UserFeedErrorCode.FEED_NOT_FOUND);
        }

        Page<FeedComment> feedComments = feedCommentService.getFeedComments(
            Long.valueOf(feedId), pageable);
        List<FeedCommentResponse> comments = new ArrayList<>();

        for (FeedComment feedComment : feedComments) {
            comments.add(getFeedCommentResponse(user, feedComment));
        }

        return ApiUtils.success(PageResponse.<FeedCommentResponse>builder()
            .lastPage(feedComments.isLast())
            .firstPage(feedComments.isFirst())
            .totalPages(feedComments.getTotalPages())
            .totalElements(feedComments.getTotalElements())
            .size(pageable.getPageSize())
            .currentPage(pageable.getPageNumber())
            .content(comments)
            .build());
    }

    @PatchMapping(value = "/{feedId}/comments/{commentId}")
    public ApiResult<FeedCommentResponse> updateFeedComment(@PathVariable("feedId") String feedId
        , @PathVariable("commentId") String commentId
        , @RequestPart("feedComment") FeedCommentDto updateFeedComment) {

        User user = getUser();
        FeedComment feedComment = feedCommentService.getFeedComment(Long.valueOf(commentId));

        if (!feedComment.getUser().getId().equals(user.getId())) {
            throw new UserFeedException(UserFeedErrorCode.FEED_USER_NOT_MATCH);
        } else {
            feedCommentService.updateFeedComment(Long.valueOf(commentId), updateFeedComment);
        }

        return ApiUtils.success(FeedCommentResponse.builder()
            .userName(user.getUsername())
            .comment(updateFeedComment.getComment())
            .build());
    }

    @DeleteMapping("/{feedId}/comments/{commentId}")
    public ApiResult<FeedCommentDto> deleteFeedComment(
        @PathVariable("feedId") String feedId
        , @PathVariable("commentId") String commentId) {

        User user = getUser();

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
    public ApiResult saveFeedCommentLike(@PathVariable("feedId") String feedId
        , @PathVariable("commentId") String commentId) {
        User user = getUser();
        FeedComment feedComment = feedCommentService.getFeedComment(Long.valueOf(commentId));

        feedCommentLikeService.saveFeedCommentLike(user, feedComment);

        // TODO: 실패 케이스에 대해서 구현 필요
        return ApiUtils.success(null);
    }

    @DeleteMapping("/{feedId}/comments/{commentId}/like")
    public ApiResult deleteFeedCommentLike(@PathVariable("feedId") String feedId
        , @PathVariable("commentId") String commentId) {
        User user = getUser();
        feedCommentLikeService.deleteFeedCommentLike(user, Long.valueOf(commentId));
        return ApiUtils.success(null);
    }

    private FeedResponse getUserFeedResponse(User user, Feed feed) {

        return FeedResponse.builder()
            .userId(String.valueOf(feed.getUser().getId()))
            .feedId(String.valueOf(feed.getId()))
            .profileImagePath(feed.getUser().getProfileImage())
            .userName(feed.getUser().getUsername())
            .nickname(feed.getUser().getNickname())

            .title(feed.getTitle())
            .content(feed.getContent())
            .rcate1(feed.getRcate1())
            .rcate2(feed.getRcate2())
            .longitude(feed.getLongitude())
            .latitude(feed.getLatitude())
            .isLikeFeed(feedLikeService.getFeedLike(user, feed.getId()))

            .feedLikeCnt((long) feed.getFeedLike().size())
            .feedCommentCnt((long) feed.getFeedComment().size())
            .feedImages(feed.getFeedImage().stream()
                .map(feedImage -> feedImage.getImagePaths()).collect(Collectors.toList()))
            .build();
    }

    private FeedResponse getFeedResponse(Feed feed) {

        return FeedResponse.builder()
            .userId(String.valueOf(feed.getUser().getId()))
            .feedId(String.valueOf(feed.getId()))
            .profileImagePath(feed.getUser().getProfileImage())
            .userName(feed.getUser().getUsername())
            .nickname(feed.getUser().getNickname())

            .title(feed.getTitle())
            .content(feed.getContent())
            .rcate1(feed.getRcate1())
            .rcate2(feed.getRcate2())
            .longitude(feed.getLongitude())
            .latitude(feed.getLatitude())
//            .isLikeFeed(feedLikeService.getFeedLike(user, feed.getId()))

            .feedLikeCnt((long) feed.getFeedLike().size())
            .feedCommentCnt((long) feed.getFeedComment().size())
            .feedImages(feed.getFeedImage().stream()
                .map(feedImage -> feedImage.getImagePaths()).collect(Collectors.toList()))
            .build();
    }


    private FeedCommentResponse getFeedCommentResponse(User user, FeedComment comment) {
        return FeedCommentResponse.builder()
            .feedId(comment.getFeed().getId().toString())
            .feedCommentId(String.valueOf(comment.getId()))
            .userId(comment.getUser().getId().toString())
            .profileImagePath(comment.getUser().getProfileImage())
            .userName(comment.getUser().getUsername())
            .comment(comment.getComment())
            .feedCommentLikeCnt((long) comment.getFeedCommentLike().size())
            .isLikeFeedComment(feedCommentLikeService.isFeedCommentLike(user, comment.getId()))
            .build();
    }

    private User getUser() {
        String getUserUUID = commonRequestContext.getUuid();

        if (!StringUtils.hasText(getUserUUID)) {
            return null;
        }
        return feedService.getUser(getUserUUID);
    }
}