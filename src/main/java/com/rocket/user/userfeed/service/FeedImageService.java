package com.rocket.user.userfeed.service;

import static com.rocket.error.type.UserFeedErrorCode.FEED_IMAGE_UPLOAD_COUNT_OVER;

import com.rocket.error.exception.UserFeedException;
import com.rocket.user.user.entity.User;
import com.rocket.user.userfeed.entity.Feed;
import com.rocket.user.userfeed.entity.FeedImage;
import com.rocket.user.userfeed.repository.FeedImageRepository;
import com.rocket.utils.AwsS3Provider;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FeedImageService {

    private final FeedImageRepository feedImageRepository;
    private final AwsS3Provider awsS3Provider;
    private static final String S3_DIR_PREFIX = "feeds";

    @Transactional
    public void createFeedImage(User user, Feed feed, List<MultipartFile> multipartFiles) {
        // TODO: AWS S3 Storage에 files 올리고 files 경로들을 imagePaths에 추가

        String path = awsS3Provider.generatePath(S3_DIR_PREFIX, feed.getId());

        List<String> files = awsS3Provider.uploadFile(multipartFiles, path);
        List<FeedImage> feedImages = new ArrayList<>();

        if (files.size() <= 4) {
            for (String file : files) {
                feedImages.add(FeedImage.builder()
                    .feed(feed)
                    .imagePaths(file)
                    .build());
            }
            feedImageRepository.saveAll(feedImages);
        } else {
            throw new UserFeedException(FEED_IMAGE_UPLOAD_COUNT_OVER);
        }
    }

    public FeedImage getFeedImage(Long feedId) {
        return feedImageRepository.findByFeedId(feedId)
            .orElse(null);
    }
}
