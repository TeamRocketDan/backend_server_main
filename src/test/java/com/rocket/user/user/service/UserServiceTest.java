package com.rocket.user.user.service;


import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.MultiObjectDeleteException;
import com.rocket.config.jpa.JpaAuditingConfiguration;
import com.rocket.error.exception.UserException;
import com.rocket.user.user.dto.UpdateNickname;
import com.rocket.user.user.dto.UserMypageDto;
import com.rocket.user.user.entity.User;
import com.rocket.user.user.repository.UserRepository;
import com.rocket.user.user.repository.query.UserQueryRepository;
import com.rocket.user.user.service.impl.UserServiceImpl;
import com.rocket.utils.AwsS3Provider;
import com.rocket.utils.CommonRequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.rocket.error.type.UserErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    @Mock
    private AwsS3Provider awsS3Provider;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(userService, "BASE_URL", "https://test.com/");
    }

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

    @Nested
    @DisplayName("프로필 이미지")
    public class profile {
        User user = User.builder()
                .id(1L)
                .username("한규빈")
                .email("rbsks147@naver.com")
                .uuid("uuid")
                .profileImage("https://test.com/users/1/image")
                .deletedAt(null)
                .build();

        List<MultipartFile> multipartFiles = new ArrayList<>(
                Arrays.asList(
                        new MockMultipartFile(
                        "multipartFiles",
                        "imagefile.jpeg",
                        "image/jpeg",
                        "<<jpeg data>>".getBytes()
                        )
                )
        );

        List<String> images = new ArrayList<>(
                Arrays.asList(
                        "updateImage"
                )
        );

        @Test
        @DisplayName("프로필 이미지 수정 성공")
        public void success_updateProfile() throws Exception {
            // given
            given(commonRequestContext.getUuid())
                    .willReturn("rbsks147@naver.com");
            given(userRepository.findByUuid(anyString()))
                    .willReturn(Optional.of(user));
            given(awsS3Provider.generatePath(anyString(), anyLong()))
                    .willReturn("users/1/");
            given(awsS3Provider.uploadFile(anyList(), anyString()))
                    .willReturn(images);

            // when
            String updateProfile = userService.updateProfile(multipartFiles);

            // then
            assertEquals(updateProfile, images.get(0));
        }

        @Test
        @DisplayName("프로필 이미지 수정 실패 - 사용자를 찾을 수 없습니다.")
        public void fail_updateProfile_01() throws Exception {
            // given
            given(commonRequestContext.getUuid())
                    .willReturn("");
            given(userRepository.findByUuid(anyString()))
                    .willReturn(Optional.empty());

            // when
            UserException userException = assertThrows(UserException.class,
                    () -> userService.updateProfile(multipartFiles));

            // then
            assertEquals(userException.getErrorCode(), USER_NOT_FOUND);
        }

        @Test
        @DisplayName("프로필 이미지 수정 실패 - 기존 이미지 삭제 실패")
        public void fail_updateProfile_02() throws Exception {
            // given
            given(commonRequestContext.getUuid())
                    .willReturn("rbsks147@naver.com");
            given(userRepository.findByUuid(anyString()))
                    .willReturn(Optional.of(user));
            given(awsS3Provider.deleteFile(anyList()))
                    .willThrow(new AmazonS3Exception("이미지 삭제 실패"));

            // when
            AmazonS3Exception amazonS3Exception =
                    assertThrows(AmazonS3Exception.class,
                    () -> userService.updateProfile(multipartFiles));

            // then
            assertTrue(amazonS3Exception.getMessage().contains("이미지 삭제 실패"));
        }

        @Test
        @DisplayName("프로필 이미지 수정 실패 - 이미지 업로드 실패")
        public void fail_updateProfile_03() throws Exception {
            // given
            given(commonRequestContext.getUuid())
                    .willReturn("rbsks147@naver.com");
            given(userRepository.findByUuid(anyString()))
                    .willReturn(Optional.of(user));
            given(awsS3Provider.generatePath(anyString(), anyLong()))
                    .willReturn("users/1/");
            given(awsS3Provider.uploadFile(anyList(), anyString()))
                    .willThrow(new AmazonS3Exception("이미지 업로드 실패"));

            // when
            AmazonS3Exception amazonS3Exception =
                    assertThrows(AmazonS3Exception.class,
                            () -> userService.updateProfile(multipartFiles));

            // then
            assertTrue(amazonS3Exception.getMessage().contains("이미지 업로드 실패"));
        }
    }

    @Nested
    @DisplayName("닉네임")
    public class nickname {
        User user = User.builder()
                .id(1L)
                .username("한규빈")
                .email("rbsks147@naver.com")
                .uuid("uuid")
                .profileImage("https://test.com/users/1/image")
                .deletedAt(null)
                .build();

        User deleteUser = User.builder()
                .id(1L)
                .username("한규빈")
                .email("rbsks147@naver.com")
                .uuid("uuid")
                .profileImage("https://test.com/users/1/image")
                .deletedAt(LocalDateTime.now())
                .build();

        @Test
        @DisplayName("닉네임 수정 성공")
        public void success_updateNickname() throws Exception {
            // given
            given(commonRequestContext.getUuid())
                    .willReturn("rbsks147@naver.com");
            given(userRepository.findByUuid(anyString()))
                    .willReturn(Optional.of(user));
            given(userRepository.existsByNickname(anyString()))
                    .willReturn(false);
            UpdateNickname updateNickname = new UpdateNickname("nickname");

            // when
            String nickname = userService.updateNickname(updateNickname);

            // then
            assertEquals(updateNickname.getNickname(), nickname);
        }

        @Test
        @DisplayName("닉네임 수정 실패 - 사용자를 찾을 수 없습니다.")
        public void fail_updateNickname_01() throws Exception {
            // given
            given(commonRequestContext.getUuid())
                    .willReturn("");
            given(userRepository.findByUuid(anyString()))
                    .willReturn(Optional.empty());
            UpdateNickname updateNickname = new UpdateNickname("nickname");

            // when
            UserException userException = assertThrows(UserException.class,
                    () -> userService.updateNickname(updateNickname));

            // then
            assertEquals(userException.getErrorCode(), USER_NOT_FOUND);
        }

        @Test
        @DisplayName("닉네임 수정 실패 - 이미 탈퇴한 회원입니다.")
        public void fail_updateNickname_02() throws Exception {
            // given
            given(commonRequestContext.getUuid())
                    .willReturn("rbsks147@naver.com");
            given(userRepository.findByUuid(anyString()))
                    .willReturn(Optional.of(deleteUser));
            UpdateNickname updateNickname = new UpdateNickname("nickname");

            // when
            UserException userException = assertThrows(UserException.class,
                    () -> userService.updateNickname(updateNickname));

            // then
            assertEquals(userException.getErrorCode(), USER_DELETED_AT);
        }

        @Test
        @DisplayName("닉네임 수정 실패 - 이미 등록된 닉네임입니다.")
        public void fail_updateNickname_03() throws Exception {
            // given
            given(commonRequestContext.getUuid())
                    .willReturn("rbsks147@naver.com");
            given(userRepository.findByUuid(anyString()))
                    .willReturn(Optional.of(user));
            given(userRepository.existsByNickname(anyString()))
                    .willReturn(true);
            UpdateNickname updateNickname = new UpdateNickname("nickname");

            // when
            UserException userException = assertThrows(UserException.class,
                    () -> userService.updateNickname(updateNickname));

            // then
            assertEquals(userException.getErrorCode(), USER_EXISTS_NICKNAME);
        }
    }
}
