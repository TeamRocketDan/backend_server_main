package com.rocket.user.user.service;

import com.rocket.utils.PagingResponse;
import org.springframework.data.domain.Pageable;

public interface FollowService {

    void following(Long followingUserId);
    void unFollowing(Long followingUserId);
    void unFollower(Long followerUserId);
    PagingResponse followerList(Pageable pageable);
    PagingResponse followingList(Pageable pageable);
}
