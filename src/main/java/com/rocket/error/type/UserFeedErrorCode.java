package com.rocket.error.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserFeedErrorCode {
    FEED_NOT_FOUND(HttpStatus.NOT_FOUND, "피드를 찾을 수 없습니다."),
    FEED_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    FEED_USER_NOT_MATCH(HttpStatus.BAD_REQUEST, "피드 작성자가 아닙니다."),
    FEED_COMMENT_USER_NOT_MATCH(HttpStatus.BAD_REQUEST, "피드 작성자가 아닙니다."),
//    FEED_ID_AND_COMMENT_ID_NOT_MATCH(HttpStatus.BAD_REQUEST, "해당 피드에 작성된 댓글이 아닙니다."),
    UPLOAD_AT_LEAST_ONE_IMAGE(HttpStatus.BAD_REQUEST, "이미지를 최소 한장 이상 업로드 해야 합니다."),
    FEED_IMAGE_UPLOAD_COUNT_OVER(HttpStatus.BAD_REQUEST, "이미지는 최대 4장 업로드 가능합니다."),
    FEED_IMAGE_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드 실패"),
    FEED_CREATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "피드 생성에 실패했습니다."),
    FEED_CONTENTS_LIMIT_2048(HttpStatus.INTERNAL_SERVER_ERROR, "내용은 최대 2048자 까지 작성 가능합니다."),

    FEED_UPDATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "피드 수정에 실패했습니다."),
    FEED_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "피드 삭제에 실패했습니다."),
    FEED_ALREADY_FEED_LIKE(HttpStatus.BAD_REQUEST, "이미 '좋아요'가 된 피드입니다."),
    FEED_ALREADY_FEED_LIKE_CANCEL(HttpStatus.BAD_REQUEST, "좋아요 하지 않은 피드입니다."),
    FEED_LIKE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "피드 좋아요에 실패했습니다"),

    FEED_COMMENT_CREATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "댓글 생성에 실패했습니다."),
    FEED_COMMENT_LIMIT_1000(HttpStatus.INTERNAL_SERVER_ERROR, "댓글은 최대 1000자 까지 작성 가능합니다."),
    FEED_COMMENT_UPDATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "댓글 수정에 실패했습니다."),
    FEED_COMMENT_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "댓글 삭제에 실패했습니다."),
    FEED_COMMENT_ALREADY_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "댓글 삭제에 실패했습니다."),
    FEED_COMMENT_ALREADY_FEED_LIKE(HttpStatus.BAD_REQUEST, "이미 '좋아요'가 된 댓글입니다."),
    FEED_COMMENT_ALREADY_FEED_LIKE_CANCEL(HttpStatus.BAD_REQUEST, "좋아요 하지 않은 댓글입니다."),
    FEED_COMMENT_LIKE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "댓글 좋아요에 실패했습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
