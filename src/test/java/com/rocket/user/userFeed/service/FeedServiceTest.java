package com.rocket.user.userFeed.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.rocket.config.oauth2.entity.ProviderType;
import com.rocket.config.oauth2.entity.RoleType;
import com.rocket.user.user.entity.User;
import com.rocket.user.user.repository.UserRepository;
import com.rocket.user.userfeed.dto.FeedDto;
import com.rocket.user.userfeed.dto.FeedSearchCondition;
import com.rocket.user.userfeed.entity.Feed;
import com.rocket.user.userfeed.repository.FeedRepository;
import com.rocket.user.userfeed.service.FeedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

//@Import({JpaAuditingConfiguration.class})
//@ExtendWith(MockitoExtension.class)
@ActiveProfiles("dev")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FeedServiceTest {

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeedService feedService;

    @Nested
    @DisplayName("피드 테스트")
    public class feedTest {

        User user1;

        FeedDto myFirstFeed;

        FeedSearchCondition feedSearchCondition;

        @BeforeEach
        void init() {
            user1 = userRepository.findById(7L)
                .orElseThrow(()-> new RuntimeException("NOT FOUND USER"));

            myFirstFeed = FeedDto.builder()
                .title("나의 첫 여행")
                .content("여행을 추억합니다 :)")
                .rcate1("서울시")
                .rcate2("강남구")
//                .rcate3("신사동")
                .longitude("37.524567")
                .latitude("127.037444")
                .build();

            feedSearchCondition = FeedSearchCondition.builder()
                .rcate1("서울시")
                .rcate2("강남구")
                .build();

        }

        @Test
        @DisplayName("피드 만들기")
        public void success_createFeed() {
            //when
            FeedDto myFeed = feedService.createFeed(user1, myFirstFeed);
            //then
            assertEquals(myFeed.getTitle(), myFirstFeed.getTitle());
            assertEquals(myFeed.getContent(), myFirstFeed.getContent());
            assertEquals(myFeed.getRcate1(), myFirstFeed.getRcate1());
            assertEquals(myFeed.getRcate2(), myFirstFeed.getRcate2());
            assertEquals(myFeed.getLongitude(), myFirstFeed.getLongitude());
            assertEquals(myFeed.getLatitude(), myFirstFeed.getLatitude());
        }

        @Test
        @DisplayName("피드 업데이트 성공")
        public void success_updateFeed() {
            //when
            FeedDto mySecondFeed = FeedDto.builder()
                .title("나의 두번째 여행")
                .content("여행을 추억합니다 :)")
                .rcate1("서울시")
                .rcate2("강남구")
                .rcate3("신사동")
                .longitude("37.524567")
                .latitude("127.037444")
                .build();
            FeedDto myFeed = feedService.updateFeed(3L, mySecondFeed);

            //then
            assertEquals(myFeed.getTitle(), mySecondFeed.getTitle());
            assertEquals(myFeed.getContent(), mySecondFeed.getContent());
            assertEquals(myFeed.getRcate1(), mySecondFeed.getRcate1());
            assertEquals(myFeed.getRcate2(), mySecondFeed.getRcate2());
            assertEquals(myFeed.getLongitude(), mySecondFeed.getLongitude());
            assertEquals(myFeed.getLatitude(), mySecondFeed.getLatitude());
        }

        @Test
        @DisplayName("피드 조회")
        public void success_getFeed() {
            Feed myFeed = feedService.getFeed(7L);
            System.out.println(myFeed);
        }

        @Test
        @DisplayName("피드 지역 검색 && 조회")
        @Transactional
        public void success_getFeeds() {
            Page<Feed> feeds = feedService.getFeeds(user1, feedSearchCondition,
                Pageable.ofSize(10));
            for (Feed feed : feeds) {
                System.out.println(feed);
                System.out.println(feed.getUser().getId());
                System.out.println(feed.getUser().getUsername());
            }
        }

        @Test
        @DisplayName("피드 삭제")
        public void success_deleteFeed() {
            feedService.deleteFeed(user1.getId(), 3L);
        }
    }
}
