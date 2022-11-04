package com.rocket.user.userFeed.repository;

import com.rocket.config.jpa.JpaAuditingConfiguration;
import com.rocket.config.querydsl.CustomMySQL8InnoDBDialect;
import com.rocket.config.querydsl.QueryDslConfiguration;
import com.rocket.user.user.entity.User;
import com.rocket.user.user.repository.UserRepository;
import com.rocket.user.userfeed.dto.FeedCommentQDto;
import com.rocket.user.userfeed.dto.FeedListDto;
import com.rocket.user.userfeed.dto.FeedSearchCondition;
import com.rocket.user.userfeed.repository.FeedImageRepository;
import com.rocket.user.userfeed.repository.query.FeedQueryRepository;
import org.hibernate.dialect.MySQL57Dialect;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import({
        FeedQueryRepository.class,
        JpaAuditingConfiguration.class,
        QueryDslConfiguration.class,
        MySQL57Dialect.class,
        CustomMySQL8InnoDBDialect.class
})
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class FeedRepositoryTest {

    @Autowired
    private FeedQueryRepository feedQueryRepository;

    @Autowired
    private FeedImageRepository feedImageRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    @DisplayName("boolean expressoin test")
    @Transactional
    @Rollback(value = false)
    public void test() throws Exception {
        // given
        User user = userRepository.findById(7L)
                .orElseThrow(() -> new RuntimeException(""));
        FeedSearchCondition feedSearchCondition = FeedSearchCondition.builder()
                .rcate1("서울시")
                .rcate2("강남구")
                .build();
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<FeedListDto> feeds = feedQueryRepository.findByRcate1EqualsAndRcate2EqualsOrderByCreatedAtDesc(
                feedSearchCondition,
                user,
                pageRequest,
                true);

        // then
        for (FeedListDto feed : feeds) {
            System.out.println(feed);
        }
    }

    @Test
    @DisplayName("comment test")
    @Transactional
    @Rollback(value = false)
    public void commentTest() throws Exception {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        User user = userRepository.findById(2L)
                .orElseThrow(() -> new RuntimeException(""));

        // when
        Page<FeedCommentQDto> feedCommentQDtos = feedQueryRepository.feedCommentFindByFeedId(56L, user, pageRequest);


        // then
        for (FeedCommentQDto feedCommentQDto : feedCommentQDtos) {
            System.out.println(feedCommentQDto);
        }

    }
}
