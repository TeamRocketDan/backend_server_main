package com.rocket.user.user.service.impl;

import com.rocket.error.exception.UserException;
import com.rocket.user.user.entity.Follow;
import com.rocket.user.user.entity.User;
import com.rocket.user.user.repository.FollowRepository;
import com.rocket.user.user.repository.UserRepository;
import com.rocket.user.user.service.FollowService;
import com.rocket.utils.CommonRequestContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.rocket.error.type.UserErrorCode.*;

@Repository
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
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
    public void unFollowing(Long followerUserId) {
        User following = getUserByUuid(commonRequestContext.getUuid());
        User follower = getUserById(followerUserId);

        followRepository.deleteByFollowerAndFollowing(follower, following);
    }

    @Override
    @Transactional
    public void unFollower(Long followingUserId) {
        User follower = getUserByUuid(commonRequestContext.getUuid());
        User following = getUserById(followingUserId);

        followRepository.deleteByFollowerAndFollowing(follower, following);
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
