package com.rocket.user.userfeed.service;

import com.rocket.user.userfeed.dto.FeedDto;
import com.rocket.user.userfeed.entity.FeedImage;
import com.rocket.user.userfeed.repository.FeedImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FeedImageService {

    private final FeedImageRepository feedImageRepository;


    @Transactional
    public FeedImage createFeedImage(FeedDto FeedDto, MultipartFile files) {
        // TODO: AWS S3 Storage에 files 올리고 files 경로들을 imagePaths에 추가

        return feedImageRepository.save(FeedImage.builder()
//            .feed(FeedDto)
//        List<String> files = awsS3Provider.uploadFile(multipartFiles, path);
            .build());
    }

    public FeedImage getFeedImage(Long feedId) {
        return feedImageRepository.findByFeedId(feedId)
            .orElse(null);
    }
}
