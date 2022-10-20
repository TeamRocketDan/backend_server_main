package com.rocket.user.user.service;


import com.rocket.config.jpa.JpaAuditingConfiguration;
import com.rocket.error.exception.UserException;
import com.rocket.error.type.UserErrorCode;
import com.rocket.user.user.dto.UserMypageDto;
import com.rocket.user.user.entity.User;
import com.rocket.user.user.repository.UserRepository;
import com.rocket.user.user.repository.query.UserQueryRepository;
import com.rocket.user.user.service.impl.UserServiceImpl;
import com.rocket.utils.CommonRequestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.rocket.error.type.UserErrorCode.USER_DELETED_AT;
import static com.rocket.error.type.UserErrorCode.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@Import({JpaAuditingConfiguration.class})
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserQueryRepository userQueryRepository;
    @Mock
    private CommonRequestContext commonRequestContext;

    @InjectMocks
    private UserServiceImpl userService;

    @Nested
    @DisplayName("마이 페이지")
    public class mypage {
        User user = User.builder()
                .id(1L)
                .username("한규빈")
                .email("rbsks147@naver.com")
                .uuid("uuid")
                .deletedAt(null)
                .build();

        User deletedAtUser = User.builder()
                .id(1L)
                .username("한규빈")
                .email("rbsks147@naver.com")
                .uuid("uuid")
                .deletedAt(LocalDateTime.now())
                .build();

        UserMypageDto userMypageDto = UserMypageDto.builder()
                .userId(1L)
                .username("한규빈")
                .email("rbsks147@naver.com")
                .nickname("규난")
                .follower(10L)
                .following(10L)
                .build();
        @Test
        @DisplayName("마이 페이지 성공")
        public void success_mypage() throws Exception {
            // given
            given(commonRequestContext.getUuid())
                    .willReturn("uuid");
            given(userRepository.findByUuid(anyString()))
                    .willReturn(Optional.of(user));
            given(userQueryRepository.findById(anyLong()))
                    .willReturn(Optional.of(userMypageDto ));

            // when
            UserMypageDto mypage = userService.mypage();

            // then
            assertEquals(mypage.getUserId(), userMypageDto.getUserId());
            assertEquals(mypage.getEmail(), userMypageDto.getEmail());
            assertEquals(mypage.getUsername(), userMypageDto.getUsername());
            assertEquals(mypage.getNickname(), userMypageDto.getNickname());
            assertEquals(mypage.getFollower(), userMypageDto.getFollower());
            assertEquals(mypage.getFollowing(), userMypageDto.getFollowing());
        }

        @Test
        @DisplayName("마이 페이지 실패 - 사용자를 찾을 수 없습니다.")
        public void fail_mypage_01() throws Exception {
            // given
            given(commonRequestContext.getUuid())
                    .willReturn("uuid");
            given(userRepository.findByUuid(anyString()))
                    .willReturn(Optional.empty());

            // when
            UserException userException = assertThrows(UserException.class,
                    () -> userService.mypage());

            // then
            assertEquals(userException.getErrorCode(), USER_NOT_FOUND);
        }

        @Test
        @DisplayName("마이 페이지 실패 - 이미 탈퇴한 사용자 입니다.")
        public void fail_mypage_02() throws Exception {
            // given
            given(commonRequestContext.getUuid())
                    .willReturn("uuid");
            given(userRepository.findByUuid(anyString()))
                    .willReturn(Optional.of(deletedAtUser));

            // when
            UserException userException = assertThrows(UserException.class,
                    () -> userService.mypage());

            // then
            assertEquals(userException.getErrorCode(), USER_DELETED_AT);
        }
    }
}
