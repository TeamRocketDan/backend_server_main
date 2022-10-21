package com.rocket.user.user.service;

public interface FollowService {

    void following(Long followingUserId);
    void unFollowing(Long followerUserId);
    void unFollower(Long followingUserId);
}
