package com.rocket.user.user.controller;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.rocket.error.exception.UserException;
import com.rocket.error.handler.GlobalExceptionHandler;
import com.rocket.error.type.UserErrorCode;
import com.rocket.user.user.dto.UpdateNickname;
import com.rocket.user.user.dto.UserMypageDto;
import com.rocket.user.user.entity.User;
import com.rocket.user.user.service.FollowService;
import com.rocket.user.user.service.UserService;
import com.rocket.utils.CommonRequestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.*;

import static com.rocket.error.type.UserErrorCode.*;
import static com.rocket.utils.JsonUtils.toJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(
        {
                CommonRequestContext.class,
                UserException.class,
                GlobalExceptionHandler.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @MockBean
    private UserService userService;
    @MockBean
    private FollowService followService;

    @Autowired
    private MockMvc mockMvc;

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeEach
    public void setup() {
        UserController userController = new UserController(userService, followService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(
                        GlobalExceptionHandler.class
                )
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver()
                )
                .setViewResolvers(
                        ((viewName, locale) -> new MappingJackson2JsonView())
                )
                .build();

        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
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
            given(userService.mypage())
                    .willReturn(userMypageDto);

            // when

            // then
            mockMvc.perform(get("/api/v1/users/mypage")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.result").isNotEmpty())
                    .andDo(print());
        }

        @Test
        @DisplayName("마이 페이지 실패 - 사용자를 찾을 수 없습니다.")
        public void fail_mypage_01() throws Exception {
            // given
            doThrow(new UserException(USER_NOT_FOUND)).when(userService)
                            .mypage();

            // when

            // then
            mockMvc.perform(get("/api/v1/users/mypage")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.result").isEmpty())
                    .andExpect(jsonPath("$.errorMessage").value(USER_NOT_FOUND.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("마이 페이지 실패 - 이미 탈퇴한 사용자 입니다.")
        public void fail_mypage_02() throws Exception {
            // given
            doThrow(new UserException(USER_DELETED_AT)).when(userService)
                    .mypage();

            // when

            // then
            mockMvc.perform(get("/api/v1/users/mypage")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.result").isEmpty())
                    .andExpect(jsonPath("$.errorMessage").value(USER_DELETED_AT.getMessage()))
                    .andDo(print());
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

        List<String> images = new ArrayList<>(
                Arrays.asList(
                        "updateImage"
                )
        );

        List<MockMultipartFile> multipartFiles = new ArrayList<>(
                Arrays.asList(
                        new MockMultipartFile(
                                "multipartFiles",
                                "imagefile.jpeg",
                                "image/jpeg",
                                "<<jpeg data>>".getBytes()
                        )
                )
        );

        @Test
        @DisplayName("프로필 이미지 수정 성공")
        public void success_updateProfile() throws Exception {
            // given
            given(userService.updateProfile(anyList()))
                    .willReturn(images.get(0));

            // when

            // then
            mockMvc.perform(multipart("/api/v1/users/profileImage")
                                    .file(multipartFiles.get(0))
                                    .with(request -> {
                                        request.setMethod("PATCH");
                                        return  request;
                                    })
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.result.profileImagePath").value(images.get(0)))
                    .andDo(print());
        }

        @Test
        @DisplayName("프로필 이미지 수정 실패 - 사용자를 찾을 수 없습니다.")
        public void fail_updateProfile_01() throws Exception {
            // given
           doThrow(new UserException(USER_NOT_FOUND)).when(userService)
                           .updateProfile(anyList());

            // when

            // then
            mockMvc.perform(multipart("/api/v1/users/profileImage")
                            .file(multipartFiles.get(0))
                            .with(request -> {
                                request.setMethod("PATCH");
                                return  request;
                            })
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.result").isEmpty())
                    .andExpect(jsonPath("$.errorMessage").value(USER_NOT_FOUND.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("프로필 이미지 수정 실패 - 기존 이미지 삭제 실패")
        public void fail_updateProfile_02() throws Exception {
            // given
            doThrow(new AmazonS3Exception("이미지 삭제 실패")).when(userService)
                    .updateProfile(anyList());

            // when

            // then
            mockMvc.perform(multipart("/api/v1/users/profileImage")
                            .file(multipartFiles.get(0))
                            .with(request -> {
                                request.setMethod("PATCH");
                                return  request;
                            })
                    )
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.result").isEmpty())
                    .andDo(print());
        }

        @Test
        @DisplayName("프로필 이미지 수정 실패 - 이미지 업로드 실패")
        public void fail_updateProfile_03() throws Exception {
            // given
            doThrow(new AmazonS3Exception("이미지 업로드 실패")).when(userService)
                    .updateProfile(anyList());

            // when

            // then
            mockMvc.perform(multipart("/api/v1/users/profileImage")
                            .file(multipartFiles.get(0))
                            .with(request -> {
                                request.setMethod("PATCH");
                                return  request;
                            })
                    )
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.result").isEmpty())
                    .andDo(print());
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
        @DisplayName("닉네임 파라미터 검증")
        public void validate() throws Exception {
            // given
            List<String> errors = new ArrayList<>(List.of(
                    "닉네임은 필수 입력 사항입니다."
            ));

            UpdateNickname updateNickname = new UpdateNickname();

            // when
            Set<ConstraintViolation<UpdateNickname>> violations =
                    validator.validate(updateNickname);

            // then
            violations.forEach(
                    error -> assertThat(error.getMessage()).isIn(errors)
            );
        }

        @Test
        @DisplayName("닉네임 수정 성공")
        public void success_updateNickname() throws Exception {
            // given
            given(userService.updateNickname(any()))
                    .willReturn("nickname");

            // when

            // then
            mockMvc.perform(patch("/api/v1/users/nickname")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                            toJson(
                                    new HashMap<>() {{
                                        put("nickname", "nickname");
                                    }}
                            )
                    ))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.result.nickname").value("nickname"))
                    .andDo(print());
        }

        @Test
        @DisplayName("닉네임 수정 실패 - 사용자를 찾을 수 없습니다.")
        public void fail_updateNickname_01() throws Exception {
            // given
            doThrow(new UserException(USER_NOT_FOUND)).when(userService)
                            .updateNickname(any());

            // when

            // then
            mockMvc.perform(patch("/api/v1/users/nickname")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(
                                    toJson(
                                            new HashMap<>() {{
                                                put("nickname", "nickname");
                                            }}
                                    )
                            ))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.result").isEmpty())
                    .andExpect(jsonPath("$.errorMessage").value(USER_NOT_FOUND.getMessage()))
                    .andDo(print());

        }

        @Test
        @DisplayName("닉네임 수정 실패 - 이미 탈퇴한 회원입니다.")
        public void fail_updateNickname_02() throws Exception {
            // given
            doThrow(new UserException(USER_DELETED_AT)).when(userService)
                    .updateNickname(any());

            // when

            // then
            mockMvc.perform(patch("/api/v1/users/nickname")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(
                                    toJson(
                                            new HashMap<>() {{
                                                put("nickname", "nickname");
                                            }}
                                    )
                            ))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.result").isEmpty())
                    .andExpect(jsonPath("$.errorMessage").value(USER_DELETED_AT.getMessage()))
                    .andDo(print());
        }

        @Test
        @DisplayName("닉네임 수정 실패 - 이미 등록된 닉네임입니다.")
        public void fail_updateNickname_03() throws Exception {
            // given
            doThrow(new UserException(USER_EXISTS_NICKNAME)).when(userService)
                    .updateNickname(any());

            // when

            // then
            mockMvc.perform(patch("/api/v1/users/nickname")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(
                                    toJson(
                                            new HashMap<>() {{
                                                put("nickname", "nickname");
                                            }}
                                    )
                            ))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.result").isEmpty())
                    .andExpect(jsonPath("$.errorMessage").value(USER_EXISTS_NICKNAME.getMessage()))
                    .andDo(print());
        }
    }
}
