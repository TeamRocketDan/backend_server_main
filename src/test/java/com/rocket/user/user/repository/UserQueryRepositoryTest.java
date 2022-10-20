package com.rocket.user.user.repository;

import com.rocket.config.jpa.JpaAuditingConfiguration;
import com.rocket.config.querydsl.CustomMySQL8InnoDBDialect;
import com.rocket.config.querydsl.QueryDslConfiguration;
import com.rocket.error.exception.UserException;
import com.rocket.error.type.UserErrorCode;
import com.rocket.user.user.dto.UserMypageDto;
import com.rocket.user.user.repository.query.UserQueryRepository;
import org.hibernate.dialect.MySQL57Dialect;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Transactional
@Import({
        UserQueryRepository.class,
        JpaAuditingConfiguration.class,
        QueryDslConfiguration.class,
        MySQL57Dialect.class,
        CustomMySQL8InnoDBDialect.class
})
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class UserQueryRepositoryTest {

    @Autowired
    private UserQueryRepository userQueryRepository;

    @Test
    @DisplayName("mypage query test")
    public void mypageQuery() throws Exception {
        // given

        // when
        UserMypageDto userMypageDto = userQueryRepository.findById(1L)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // then
        assertEquals(userMypageDto.getUserId(), 1L);
        assertEquals(userMypageDto.getEmail(), "rbsks147@naver.com");
        assertEquals(userMypageDto.getUsername(), "한규빈");
    }
}
