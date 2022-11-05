package com.rocket.user.user.service.impl;

import com.rocket.error.exception.UserException;
import com.rocket.user.user.dto.FollowerDto;
import com.rocket.user.user.dto.FollowingDto;
import com.rocket.user.user.entity.Follow;
import com.rocket.user.user.entity.User;
import com.rocket.user.user.repository.FollowRepository;
import com.rocket.user.user.repository.UserRepository;
import com.rocket.user.user.repository.query.FollowQueryRepository;
import com.rocket.user.user.service.FollowService;
import com.rocket.utils.CommonRequestContext;
import com.rocket.utils.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.rocket.error.type.UserErrorCode.*;

@Repository
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final FollowQueryRepository followQueryRepository;
    private final CommonRequestContext commonRequestContext;

    @Override
    @Transactional
    public void following(Long followingUserId) {
        User following = getUserByUuid(commonRequestContext.getUuid());
        User follower = getUserById(followingUserId);

        if (Objects.equals(follower.getId(), following.getId())) {
            throw new UserException(USER_IMPOSSIBLE_FOLLOWING);
        }

        boolean exists = followRepository.existsByFollowerAndFollowing(follower, following);
        if (exists) {
            throw new UserException(USER_ALREADY_FOLLOWING);
        }

        Follow follow = Follow.builder()
                .following(following)
                .follower(follower)
                .build();

        followRepository.save(follow);
    }

    @Override
    @Transactional
    public void unFollowing(Long followingUserId) {
        User following = getUserByUuid(commonRequestContext.getUuid());
        User follower = getUserById(followingUserId);

        followQueryRepository.deleteByFollowerAndFollowing(follower, following);
    }

    @Override
    @Transactional
    public void unFollower(Long followerUserId) {
        User follower = getUserByUuid(commonRequestContext.getUuid());
        User following = getUserById(followerUserId);

        followQueryRepository.deleteByFollowerAndFollowing(follower, following);
    }

    @Override
    @Transactional(readOnly = true)
    public PagingResponse followerList(Pageable pageable) {
        User user = getUserByUuid(commonRequestContext.getUuid());

        Page<Follow> followers = followQueryRepository.findByFollower(user, pageable);

        return PagingResponse.fromEntity(
                followers.map(follow -> FollowerDto.fromEntity(follow))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PagingResponse followingList(Pageable pageable) {
        User user = getUserByUuid(commonRequestContext.getUuid());

        Page<Follow> followings = followQueryRepository.findByFollowing(user, pageable);

        return PagingResponse.fromEntity(
                followings.map(follow -> FollowingDto.fromEntity(follow))
        );
    }

    private User getUserByUuid(String uuid) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        validateUser(user);

        return user;
    }

    private User getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        validateUser(user);

        return user;
    }

    private void validateUser(User user) {
        if (user.getDeletedAt() != null) {
            throw new UserException(USER_DELETED_AT);
        }
    }
}
