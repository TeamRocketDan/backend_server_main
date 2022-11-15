package com.rocket.user.userfeed.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FeedListDto {

    private Long userId;
    private Long feedId;
    private String profileImagePath;
    private String userName;
    private String nickname;
    private String email;
    private String title;
    private String content;
    private String rcate1;
    private String rcate2;
    private String longitude;
    private String latitude;
    private Boolean isLikeFeed;
    private Boolean isFollow;
    private Long feedLikeCnt;
    private Long feedCommentCnt;
    private List<String> feedImages = new ArrayList<>();

    @QueryProjection
    public FeedListDto(Long userId, Long feedId, String profileImagePath, String userName,
        String nickname, String email, String title, String content, String rcate1, String rcate2,
        String longitude, String latitude, Boolean isLikeFeed, Boolean isFollow, Long feedLikeCnt,
        Long feedCommentCnt) {

        this.userId = userId;
        this.feedId = feedId;
        this.profileImagePath = profileImagePath;
        this.userName = userName;
        this.nickname = nickname;
        this.email = email;
        this.title = title;
        this.content = content;
        this.rcate1 = rcate1;
        this.rcate2 = rcate2;
        this.longitude = longitude;
        this.latitude = latitude;
        this.isLikeFeed = isLikeFeed;
        this.isFollow = isFollow;
        this.feedLikeCnt = feedLikeCnt;
        this.feedCommentCnt = feedCommentCnt;
    }

    public void addImage(String feedImage) {
        this.feedImages.add(feedImage);
    }
}
