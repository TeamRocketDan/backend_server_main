package com.rocket.user.user.controller;

import com.rocket.error.exception.UserException;
import com.rocket.error.type.UserErrorCode;
import com.rocket.user.user.dto.UpdateNickname;
import com.rocket.user.user.dto.UserMypageDto;
import com.rocket.user.user.service.FollowService;
import com.rocket.user.user.service.UserService;
import com.rocket.utils.ApiUtils.ApiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

import static com.rocket.error.type.UserErrorCode.USER_PROFILE_LIST_NOT_ZERO;
import static com.rocket.utils.ApiUtils.success;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FollowService followService;

    @GetMapping("/mypage")
    public ApiResult<UserMypageDto> mypage() {

        return success(userService.mypage());
    }

    @PatchMapping("/profileImage")
    public ApiResult<HashMap<String, Object>> updateProfileImage(List<MultipartFile> multipartFiles) {

        if (multipartFiles == null || multipartFiles.size() <= 0) {
            throw new UserException(USER_PROFILE_LIST_NOT_ZERO);
        }

        return success(new HashMap<>() {{
            put("profileImagePath", userService.updateProfile(multipartFiles));
        }});
    }

    @PostMapping("/{userId}/following")
    public ApiResult<?> following(
            @PathVariable("userId") Long userId) {

        followService.following(userId);
        return success(null);
    }

    @DeleteMapping("/{userId}/following")
    public ApiResult<?> unFollowing(
            @PathVariable("userId") Long userId) {

        followService.unFollowing(userId);
        return success(null);
    }

    @DeleteMapping("/{userId}/follower")
    public ApiResult<?> unFollower(
            @PathVariable("userId") Long userId) {

        followService.unFollower(userId);
        return success(null);
    }

    @PatchMapping("/nickname")
    public ApiResult<HashMap<String, Object>> updateNickname(@RequestBody @Validated UpdateNickname updateNickname) {

        return success(
                new HashMap<>() {{
                    put("nickname", userService.updateNickname(updateNickname));
                }}
        );
    }

    @GetMapping("/follower")
    public ApiResult followerList (Pageable pageable) {

        return success(followService.followerList(pageable));
    }

    @GetMapping("/following")
    public ApiResult followingList (Pageable pageable) {

        return success(followService.followingList(pageable));
    }
}
