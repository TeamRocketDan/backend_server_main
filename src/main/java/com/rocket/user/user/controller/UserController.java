package com.rocket.user.user.controller;

import com.rocket.user.user.service.FollowService;
import com.rocket.user.user.service.UserService;
import com.rocket.utils.ApiUtils.ApiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.rocket.utils.ApiUtils.success;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FollowService followService;

    @GetMapping("/mypage")
    public ApiResult mypage() {

        return success(userService.mypage());
    }

    @PostMapping("/{userId}/following")
    public ApiResult following(
            @PathVariable("userId") Long userId) {

        followService.following(userId);
        return success(null);
    }

    @DeleteMapping("/{userId}/following")
    public ApiResult unFollowing(
            @PathVariable("userId") Long userId) {

        followService.unFollowing(userId);
        return success(null);
    }

    @DeleteMapping("/{userId}/follower")
    public ApiResult unFollower(
            @PathVariable("userId") Long userId) {

        followService.unFollower(userId);
        return success(null);
    }
}
