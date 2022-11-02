package com.rocket.user.userFeed.service;

import com.rocket.user.user.entity.User;
import com.rocket.user.user.repository.UserRepository;
import com.rocket.user.userfeed.dto.FeedCommentDto;
import com.rocket.user.userfeed.entity.Feed;
import com.rocket.user.userfeed.entity.FeedComment;
import com.rocket.user.userfeed.repository.FeedRepository;
import com.rocket.user.userfeed.service.FeedCommentService;
import java.io.IOException;
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

//@Import({JpaAuditingConfiguration.class})
//@ExtendWith(MockitoExtension.class)
@ActiveProfiles("dev")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FeedCommentServiceTest {

    @Autowired
    private FeedRepository feedRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FeedCommentService feedCommentService;

    @Nested
    @DisplayName("피드 댓글 테스트")
    public class feedTest {

        User user1;
        Feed myFirstFeed;
        FeedCommentDto myFirstComment;
        FeedCommentDto mySecondComment;

        @BeforeEach
        void init() throws IOException {
            user1 = userRepository.findById(7L)
                .orElseThrow(() -> new RuntimeException("NOT FOUND USER"));

            myFirstFeed = feedRepository.findById(17L).orElse(null);

            myFirstComment = FeedCommentDto.builder()
                .comment("정말 좋은 여행지네요~111")
                .build();
            mySecondComment = FeedCommentDto.builder()
                .comment("정말 좋은 여행지네요~222")
                .build();
        }

        @Test
        @DisplayName("피드 댓글 달기")
        public void success_createFeedComments() {
            FeedComment likeFeed = feedCommentService.createFeedComment(user1, myFirstFeed,
                myFirstComment);
        }

        @Test
        @DisplayName("피드 댓글 수정 하기")
        public void success_updateFeedComment() {
            FeedComment findFeedComment = feedCommentService.getFeedComment(2L);

            FeedCommentDto updateFeedComment
                = feedCommentService.updateFeedComment(findFeedComment.getId(), mySecondComment);
        }

        @Test
        @DisplayName("피드 댓글 목록 조회 하기")
        public void success_getFeedComments() {
            Page<FeedComment> feedCommentPage = feedCommentService.getFeedComments(16L,
                Pageable.ofSize(10));
            for (FeedComment comment : feedCommentPage) {
                System.out.println(comment);
            }
        }

        @Test
        @DisplayName("피드 댓글 지우기")
        public void success_deleteFeedLike() {
            FeedComment deleteFeedComment = feedCommentService.getFeedComment(2L);
            feedCommentService.deleteFeedComment(deleteFeedComment);
        }

        @Test
        @DisplayName("피드 댓글 갯수")
        public void getFeedLikeCount() {
            System.out.println(feedCommentService.getCount(17L));
        }
    }
}
