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

import static com.rocket.error.type.UserErrorCode.USER_ALREADY_FOLLOWING;
import static com.rocket.error.type.UserErrorCode.USER_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final CommonRequestContext commonRequestContext;

    @Override
    @Transactional
    public void following(Long followingUserId) {
        User follower = getUserByUuid(commonRequestContext.getUuid());
        User following = getUserById(followingUserId);


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
            throw new RuntimeException("이미 탈퇴한 유저입니다.");
        }
    }
}
