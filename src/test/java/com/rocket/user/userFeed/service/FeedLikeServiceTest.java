package com.rocket.user.userFeed.service;

import com.rocket.user.user.entity.User;
import com.rocket.user.user.repository.UserRepository;
import com.rocket.user.userfeed.dto.FeedDto;
import com.rocket.user.userfeed.entity.FeedLike;
import com.rocket.user.userfeed.repository.FeedRepository;
import com.rocket.user.userfeed.service.FeedLikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

//@Import({JpaAuditingConfiguration.class})
//@ExtendWith(MockitoExtension.class)
@ActiveProfiles("dev")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FeedLikeServiceTest {

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeedLikeService feedLikeService;

    @Nested
    @DisplayName("피드 테스트")
    public class feedTest {

        User user1;
        FeedDto myFirstFeed;

        @BeforeEach
        void init() {
            user1 = userRepository.findById(7L)
                .orElseThrow(() -> new RuntimeException("NOT FOUND USER"));

            myFirstFeed = FeedDto.builder()
                .title("나의 첫 여행")
                .content("여행을 추억합니다 :)")
                .rcate1("서울시")
                .rcate2("강남구")
//                .rcate3("신사동")
                .longitude("37.524567")
                .latitude("127.037444")
                .build();
        }

        @Test
        @DisplayName("피드 좋아요")
        public void success_createFeedLike() {
            //when
            FeedLike likeFeed = feedLikeService.createFeedLike(user1, 4L);
            //then

        }

        @Test
        @DisplayName("피드 좋아요 취소")
        public void success_deleteFeedLike() {
            feedLikeService.deleteFeedLike(user1, 4L);
        }

        @Test
        @DisplayName("피드 좋아요 갯수")
        public void getFeedLikeCount() {
            System.out.println(feedLikeService.getCount(4L));
        }
    }
}
