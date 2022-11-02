package com.rocket.user.userFeed.service;

import com.rocket.user.user.entity.User;
import com.rocket.user.user.repository.UserRepository;
import com.rocket.user.userfeed.entity.FeedComment;
import com.rocket.user.userfeed.entity.FeedCommentLike;
import com.rocket.user.userfeed.repository.FeedCommentRepository;
import com.rocket.user.userfeed.repository.FeedRepository;
import com.rocket.user.userfeed.service.FeedCommentLikeService;
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
public class FeedCommentLikeServiceTest {

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeedCommentRepository feedCommentRepository;

    @Autowired
    private FeedCommentLikeService feedCommentLikeService;

    @Nested
    @DisplayName("피드 댓글 좋아요 테스트")
    public class feedTest {

        User user1;
        FeedComment feedComment;
        FeedCommentLike likeFeed;

        @BeforeEach
        void init() {
            user1 = userRepository.findById(7L)
                .orElseThrow(() -> new RuntimeException("NOT FOUND USER"));
            feedComment = feedCommentRepository.findById(3L).orElse(null);
        }

        @Test
        @DisplayName("피드 댓글 좋아요")
        public void success_createFeedLike() {
            likeFeed = feedCommentLikeService.saveFeedCommentLike(user1,
                feedComment);
        }

        @Test
        @DisplayName("피드 댓글 좋아요 취소")
        public void success_deleteFeedLike() {
            feedCommentLikeService.deleteFeedCommentLike(user1,feedComment.getId());
        }
    }
}
