package com.rocket.auth;

import com.rocket.config.jwt.AuthToken;
import com.rocket.config.jwt.AuthTokenProvider;
import com.rocket.config.oauth2.entity.RoleType;
import com.rocket.config.properties.AppProperties;
import com.rocket.user.user.repository.UserRefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static com.rocket.error.type.AuthErrorCode.INVALID_ACCESS_TOKEN;
import static com.rocket.error.type.AuthErrorCode.NOT_YET_EXPIRED_TOKEN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class AuthControllerTest {

        @Autowired
        private AppProperties appProperties;
        @Autowired
        private AuthTokenProvider authTokenProvider;
        @Autowired
        UserRefreshTokenRepository userRefreshTokenRepository;
        @Autowired
        private WebApplicationContext context;

        private MockMvc mockMvc;

        @BeforeEach
        public void setup() {

                mockMvc = MockMvcBuilders
                        .webAppContextSetup(context)
                        .build();
        }

        @Nested
        @DisplayName("리프레쉬 토큰 테스트")
        class refreshToken {

                @Test
                @DisplayName("리프레쉬 토큰 실패 - 유효한 토큰이 아닐 때")
                public void invalidAccessToken() throws Exception {
                        // given

                        // when

                        // then
                        mockMvc.perform(get("/api/v1/auth/refresh")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header(HttpHeaders.AUTHORIZATION, "Bearer dqwdqwdqdwwd"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.result").isEmpty())
                                .andExpect(jsonPath("$.errorMessage").value(INVALID_ACCESS_TOKEN.getMessage()))
                                .andDo(print());
                }

                @Test
                @DisplayName("리프레쉬 토큰 실패 - 만료되지 않은 토큰일 때")
                public void expiredAccessToken() throws Exception {
                        // given
                        Date now = new Date();
                        AuthToken accessToken = authTokenProvider.createAuthToken(
                                "testuser123",
                                RoleType.USER.getCode(),
                                new Date(now.getTime() + 1800000)
                        );

                        // when

                        // then
                        mockMvc.perform(get("/api/v1/auth/refresh")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.getToken()))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.result").isEmpty())
                                .andExpect(jsonPath("$.errorMessage").value(NOT_YET_EXPIRED_TOKEN.getMessage()))
                                .andDo(print());
                }
        }
        
}
