package com.rocket.error.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserFeedErrorCode {

    UPLOAD_AT_LEAST_ONE_IMAGE(HttpStatus.BAD_REQUEST, "이미지를 최소 한장 이상 업로드 해야 합니다."),
    FEED_CREATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "게시글 생성에 실패했습니다."),
    FEED_UPDATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "게시글 수정에 실패했습니다."),
    FEED_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "게시글 삭제에 실패했습니다."),
    FEED_ALREADY_FEED_LIKE_CANCEL(HttpStatus.BAD_REQUEST, "'좋아요'가 이미 취소 된 피드입니다."),
    FEED_ALREADY_FEED_LIKE(HttpStatus.BAD_REQUEST, "이미 '좋아요'가 된 피드입니다."),
    FEED_LIKE_FAIL(HttpStatus.BAD_REQUEST, "게시글 좋아요 실패"),
    FEED_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    FEED_IMAGE_UPLOAD_COUNT_OVER(HttpStatus.BAD_REQUEST, "이미지는 최대 4장 업로드 가능합니다."),
    FEED_IMAGE_UPLOAD_FAIL(HttpStatus.BAD_REQUEST, "이미지는 업로드 실패");

    private final HttpStatus httpStatus;
    private final String message;
}
