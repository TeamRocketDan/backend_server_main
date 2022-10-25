package com.rocket.user.user.service;

import com.rocket.user.user.dto.UpdateNickname;
import com.rocket.user.user.dto.UserMypageDto;
import com.rocket.utils.PagingResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    UserMypageDto mypage();
    String updateProfile(List<MultipartFile> multipartFiles);
    String updateNickname(UpdateNickname updateNickname);
}
