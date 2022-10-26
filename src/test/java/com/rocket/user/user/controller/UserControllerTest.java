package com.rocket.user.user.controller;

import static com.rocket.error.type.UserErrorCode.USER_DELETED_AT;
import static com.rocket.error.type.UserErrorCode.USER_NOT_FOUND;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rocket.error.exception.UserException;
import com.rocket.error.handler.GlobalExceptionHandler;
import com.rocket.user.user.dto.UserMypageDto;
import com.rocket.user.user.entity.User;
import com.rocket.user.user.service.FollowService;
import com.rocket.user.user.service.UserService;
import com.rocket.utils.CommonRequestContext;
import java.time.LocalDateTime;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

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
            mockMvc.perform(get("/api/v1/user/mypage")
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
            mockMvc.perform(get("/api/v1/user/mypage")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.result").isEmpty())
                .andExpect(jsonPath("$.errorMessage").value(USER_NOT_FOUND.getMessage()))
                .andDo(print());
        }

        @Test
        @DisplayName("마이 페이지 실패 - 사용자를 찾을 수 없습니다.")
        public void fail_mypage_02() throws Exception {
            // given
            doThrow(new UserException(USER_DELETED_AT)).when(userService)
                .mypage();

            // when

            // then
            mockMvc.perform(get("/api/v1/user/mypage")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.result").isEmpty())
                .andExpect(jsonPath("$.errorMessage").value(USER_DELETED_AT.getMessage()))
                .andDo(print());
        }
    }

}