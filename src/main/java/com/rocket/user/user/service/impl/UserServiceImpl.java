package com.rocket.user.user.service.impl;

import com.rocket.error.exception.UserException;
import com.rocket.error.type.UserErrorCode;
import com.rocket.user.user.dto.UserDto;
import com.rocket.user.user.dto.UserMypageDto;
import com.rocket.user.user.entity.User;
import com.rocket.user.user.repository.UserRepository;
import com.rocket.user.user.repository.query.UserQueryRepository;
import com.rocket.user.user.service.UserService;
import com.rocket.utils.CommonRequestContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import static com.rocket.error.type.UserErrorCode.USER_DELETED_AT;
import static com.rocket.error.type.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;
    private final CommonRequestContext commonRequestContext;

    @Override
    @Transactional(readOnly = true)
    public UserMypageDto mypage() {
        User user = getUser(commonRequestContext.getUuid());

        return userQueryRepository.findById(user.getId())
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
    }

    private User getUser(String uuid) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        if (user.getDeletedAt() != null) {
            throw new UserException(USER_DELETED_AT);
        }

        return user;
    }
}
