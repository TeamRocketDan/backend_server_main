package com.rocket.error.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode {

    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다."),
    USER_DELETED_AT(HttpStatus.BAD_REQUEST, "이미 탈퇴한 사용자 입니다."),
    USER_ALREADY_FOLLOWING(HttpStatus.BAD_REQUEST, "이미 팔로잉한 유저입니다."),
    USER_IMPOSSIBLE_FOLLOWING(HttpStatus.BAD_REQUEST, "본인을 팔로잉 할 수 없습니다."),
    USER_PROFILE_LIST_NOT_ZERO(HttpStatus.BAD_REQUEST, "업로드 할 이미지가 없습니다."),
    USER_DELETE_FAIL_PROFILE_(HttpStatus.BAD_REQUEST, "프로필 이미지 삭제에 실패하였습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
