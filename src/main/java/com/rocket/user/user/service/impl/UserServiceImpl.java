package com.rocket.user.user.service.impl;

import com.rocket.error.exception.UserException;
import com.rocket.user.user.dto.UpdateNickname;
import com.rocket.user.user.dto.UserMypageDto;
import com.rocket.user.user.entity.User;
import com.rocket.user.user.repository.UserRepository;
import com.rocket.user.user.repository.query.UserQueryRepository;
import com.rocket.user.user.service.UserService;
import com.rocket.utils.AwsS3Provider;
import com.rocket.utils.CommonRequestContext;
import com.rocket.utils.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.rocket.error.type.UserErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;
    private final CommonRequestContext commonRequestContext;
    private final AwsS3Provider awsS3Provider;

    private static final String S3_DIR_PREFIX = "users";
    @Value("${property.s3-base-url}")
    private String BASE_URL;

    @Override
    @Transactional(readOnly = true)
    public UserMypageDto mypage() {
        User user = getUser(commonRequestContext.getUuid());

        return userQueryRepository.findById(user.getId())
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));
    }

    @Override
    @Transactional
    public String updateProfile(List<MultipartFile> multipartFiles) {
        User user = getUser(commonRequestContext.getUuid());

        deleteProfileImage(user);

        String path = awsS3Provider.generatePath(S3_DIR_PREFIX, user.getId());
        List<String> files = awsS3Provider.uploadFile(multipartFiles, path);
        user.updateProfileImageUrl(files.get(0));

        return files.get(0);
    }

    @Override
    @Transactional
    public String updateNickname(UpdateNickname updateNickname) {
        User user = getUser(commonRequestContext.getUuid());

        boolean exists = userRepository.existsByNickname(updateNickname.getNickname());
        if (exists) {
            throw new UserException(USER_EXISTS_NICKNAME);
        }

        user.updateNickname(updateNickname.getNickname());

        return user.getNickname();
    }

    private void deleteProfileImage(User user) {
        if (StringUtils.hasText(user.getProfileImage())
            && user.getProfileImage().startsWith(BASE_URL)) {
            awsS3Provider.deleteFile(new ArrayList<>(Arrays.asList(user.getProfileImage())));
        }
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
