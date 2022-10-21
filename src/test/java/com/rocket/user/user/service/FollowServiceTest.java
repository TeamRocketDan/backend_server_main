package com.rocket.user.user.service;


import com.rocket.config.jpa.JpaAuditingConfiguration;
import com.rocket.error.exception.UserException;
import com.rocket.user.user.dto.UserMypageDto;
import com.rocket.user.user.entity.User;
import com.rocket.user.user.repository.FollowRepository;
import com.rocket.user.user.repository.UserRepository;
import com.rocket.user.user.repository.query.UserQueryRepository;
import com.rocket.user.user.service.impl.FollowServiceImpl;
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

import static com.rocket.error.type.UserErrorCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Import({JpaAuditingConfiguration.class})
@ExtendWith(MockitoExtension.class)
public class FollowServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private FollowRepository followRepository;
    @Mock
    private CommonRequestContext commonRequestContext;

    @InjectMocks
    private FollowServiceImpl followService;

    @Nested
    @DisplayName("팔로잉")
    public class following {
        User user1 = User.builder()
                .id(1L)
                .username("한규빈")
                .email("rbsks147@naver.com")
                .uuid("uuid1")
                .deletedAt(null)
                .build();

        User user2 = User.builder()
                .id(2L)
                .username("한규빈")
                .email("rbsks147@gmail.com")
                .uuid("uuid2")
                .deletedAt(null)
                .build();

        User deletedAtUser1 = User.builder()
                .id(1L)
                .username("한규빈")
                .email("rbsks147@naver.com")
                .uuid("uuid1")
                .deletedAt(LocalDateTime.now())
                .build();

        @Test
        @DisplayName("팔로잉 성공")
        public void success_following() throws Exception {
            // given
            given(commonRequestContext.getUuid())
                    .willReturn("uuid1");
            given(userRepository.findByUuid(anyString()))
                    .willReturn(Optional.of(user1));
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.of(user2));
            given(followRepository.existsByFollowerAndFollowing(any(), any()))
                    .willReturn(false);

            // when
            followService.following(2L);

            // then
            verify(followRepository, times(1))
                    .save(any());

        }

        @Test
        @DisplayName("팔로잉 실패 - 사용자를 찾을 수 없습니다.")
        public void fail_following_01() throws Exception {
            // given
            given(commonRequestContext.getUuid())
                    .willReturn("uuid");
            given(userRepository.findByUuid(anyString()))
                    .willReturn(Optional.empty());

            // when
            UserException userException = assertThrows(UserException.class,
                    () -> followService.following(1L));

            // then
            assertEquals(userException.getErrorCode(), USER_NOT_FOUND);
        }

        @Test
        @DisplayName("팔로잉 실패 - 이미 탈퇴한 사용자 입니다.")
        public void fail_following_02() throws Exception {
            // given
            given(commonRequestContext.getUuid())
                    .willReturn("uuid");
            given(userRepository.findByUuid(anyString()))
                    .willReturn(Optional.of(deletedAtUser1));

            // when
            UserException userException = assertThrows(UserException.class,
                    () -> followService.following(1L));

            // then
            assertEquals(userException.getErrorCode(), USER_DELETED_AT);
        }

        @Test
        @DisplayName("팔로잉 실패 - 본인을 팔로잉 할 수 없습니다.")
        public void fail_following_03() throws Exception {
            // given
            given(commonRequestContext.getUuid())
                    .willReturn("uuid");
            given(userRepository.findByUuid(anyString()))
                    .willReturn(Optional.of(user1));
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.of(user1));

            // when
            UserException userException = assertThrows(UserException.class,
                    () -> followService.following(1L));

            // then
            assertEquals(userException.getErrorCode(), USER_IMPOSSIBLE_FOLLOWING);
        }

        @Test
        @DisplayName("팔로잉 실패 - 이미 팔로잉한 유저입니다.")
        public void fail_following_04() throws Exception {
            // given
            given(commonRequestContext.getUuid())
                    .willReturn("uuid");
            given(userRepository.findByUuid(anyString()))
                    .willReturn(Optional.of(user1));
            given(userRepository.findById(anyLong()))
                    .willReturn(Optional.of(user2));
            given(followRepository.existsByFollowerAndFollowing(any(), any()))
                    .willReturn(true);

            // when
            UserException userException = assertThrows(UserException.class,
                    () -> followService.following(1L));

            // then
            assertEquals(userException.getErrorCode(), USER_ALREADY_FOLLOWING);
        }
    }
}
