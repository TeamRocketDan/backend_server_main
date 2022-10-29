package com.rocket.user.userFeed.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.rocket.user.user.entity.User;
import com.rocket.user.user.repository.UserRepository;
import com.rocket.user.userfeed.dto.FeedDto;
import com.rocket.user.userfeed.dto.FeedSearchCondition;
import com.rocket.user.userfeed.entity.Feed;
import com.rocket.user.userfeed.service.FeedService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

//@Import({JpaAuditingConfiguration.class})
//@ExtendWith(MockitoExtension.class)
@ActiveProfiles("dev")
@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FeedServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeedService feedService;

    @Nested
    @DisplayName("피드 테스트")
    public class feedTest {

        User user1;

        Feed myFirstFeed;

        List<MultipartFile> multipartFiles;

        FeedSearchCondition feedSearchCondition;

        @BeforeEach
        void init() throws IOException {
            user1 = userRepository.findById(7L)
                .orElseThrow(() -> new RuntimeException("NOT FOUND USER"));

            myFirstFeed = Feed.builder()
                .title("나의 첫 여행")
                .content("여행을 추억합니다 :)")
                .rcate1("서울시")
                .rcate2("강남구")
                .longitude("37.524567")
                .latitude("127.037444")
                .build();

            File file1 = new File("C:\\Users\\82109\\Downloads\\platypus.jpg");
            File file2 = new File("C:\\Users\\82109\\Downloads\\platypus.jpg");
            File file3 = new File("C:\\Users\\82109\\Downloads\\platypus.jpg");
            File file4 = new File("C:\\Users\\82109\\Downloads\\platypus.jpg");
//            File file5 = new File("C:\\Users\\82109\\Downloads\\platypus.jpg");

            multipartFiles = new ArrayList<>();
            multipartFiles.add(convertFileToMultipartFile(file1));
            multipartFiles.add(convertFileToMultipartFile(file2));
            multipartFiles.add(convertFileToMultipartFile(file3));
            multipartFiles.add(convertFileToMultipartFile(file4));

            feedSearchCondition = FeedSearchCondition.builder()
                .rcate1("서울시")
                .rcate2("강남구")
                .build();
        }

        @Test
        @DisplayName("피드 만들기")
        @Rollback(value = false)
        public void success_createFeed() {
            //when
            FeedDto myFeed = feedService.createFeed(user1, myFirstFeed, multipartFiles);
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
        @Rollback(value = false)
        public void success_updateFeed() {
            //when
            FeedDto mySecondFeed = FeedDto.builder()
                .title("나의 두번째 여행")
                .content("여행을 추억합니다 :)")
                .rcate1("서울시")
                .rcate2("강남구")
                .longitude("37.524567")
                .latitude("127.037444")
                .build();
            FeedDto myFeed = feedService.updateFeed(15L, mySecondFeed);

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
            Feed myFeed = feedService.getFeed(17L);
            System.out.println(myFeed);
        }

        @Test
        @DisplayName("나의 피드 지역 검색 && 조회")
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
        @DisplayName("피드 지역 검색 && 조회")
        @Transactional
        public void success_getFeedList() {
            Page<Feed> feeds = feedService.getFeedList(feedSearchCondition,
                Pageable.ofSize(10));
            for (Feed feed : feeds) {
                System.out.println(feed);
                System.out.println(feed.getUser().getId());
                System.out.println(feed.getUser().getUsername());
            }
        }

        @Test
        @DisplayName("피드 삭제")
        @Rollback(value = false)
        public void success_deleteFeed() {
            feedService.deleteFeed(user1.getId(), 5L);
        }

        private MultipartFile convertFileToMultipartFile(File file) throws IOException {
            FileItem fileItem = new DiskFileItem("file"
                , Files.probeContentType(file.toPath())
                , false, file.getName()
                , (int) file.length(),
                file.getParentFile());

            try {
                InputStream is = new FileInputStream(file);
                OutputStream os = fileItem.getOutputStream();
                IOUtils.copy(is, os);
            } catch (IOException e) {
                throw new IOException(e);
            }
            return new CommonsMultipartFile(fileItem);
        }
    }
}
