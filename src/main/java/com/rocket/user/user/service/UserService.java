package com.rocket.user.user.service;

import com.rocket.user.user.dto.UserMypageDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    UserMypageDto mypage();
    String updateProfile(List<MultipartFile> multipartFiles);
}
