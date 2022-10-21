package com.rocket.user.user.repository;

import com.rocket.config.jpa.JpaAuditingConfiguration;
import com.rocket.config.querydsl.CustomMySQL8InnoDBDialect;
import com.rocket.config.querydsl.QueryDslConfiguration;
import com.rocket.error.exception.UserException;
import com.rocket.error.type.UserErrorCode;
import com.rocket.user.user.dto.UserMypageDto;
import com.rocket.user.user.entity.Follow;
import com.rocket.user.user.entity.User;
import com.rocket.user.user.repository.query.UserQueryRepository;
import org.hibernate.dialect.MySQL57Dialect;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static com.rocket.error.type.UserErrorCode.USER_NOT_FOUND;
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
public class FollowRepositoryTest {

    @Autowired
    private FollowRepository followRepository;

    @Autowired UserRepository userRepository;

    @Test
    @DisplayName("팔로잉 인서트 테스트")
    public void following() throws Exception {
        // given
        User following = userRepository.findById(6L)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        User follower = userRepository.findById(1L)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        Follow follow = Follow.builder()
                .following(following)
                .follower(follower)
                .build();

        // when
        followRepository.save(follow);
        boolean exists = followRepository.existsByFollowerAndFollowing(follower, following);

        //then
        assertEquals(exists, true);
    }

    @Test
    @DisplayName("언팔로잉 테스트")
    public void unFollowing() throws Exception {
        // given
        User following = userRepository.findById(6L)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        User follower = userRepository.findById(1L)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        // when
        followRepository.deleteByFollowerAndFollowing(follower, following);
        boolean exists = followRepository.existsByFollowerAndFollowing(follower, following);

        //then
        assertEquals(exists, false);
    }

    @Test
    @DisplayName("언팔로워 테스트")
    @Transactional
    @Rollback(value = false)
    public void unFollower() throws Exception {
        // given
        User following = userRepository.findById(5L)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        User follower = userRepository.findById(6L)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        // when
        followRepository.deleteByFollowerAndFollowing(follower, following);
        boolean exists = followRepository.existsByFollowerAndFollowing(follower, following);

        //then
        assertEquals(exists, false);
    }
}
