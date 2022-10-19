package com.rocket.user.user.service.impl;

import com.rocket.error.exception.UserException;
import com.rocket.error.type.UserErrorCode;
import com.rocket.user.user.dto.UserDto;
import com.rocket.user.user.entity.User;
import com.rocket.user.user.repository.UserRepository;
import com.rocket.user.user.service.UserService;
import com.rocket.utils.CommonRequestContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.rocket.error.type.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CommonRequestContext commonRequestContext;

    @Override
    @Transactional(readOnly = true)
    public UserDto mypage() {
        User user = getUser(commonRequestContext.getUuid());

        return UserDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }

    private User getUser(String uuid) {
        return userRepository.findByUuid(uuid)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
    }
}
