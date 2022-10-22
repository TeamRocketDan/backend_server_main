package com.rocket.user.userfeed.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(Include.NON_NULL)
public class FeedResponse {

    private String userId;

    private String feedId;

    private String profileImagePath;

    private String nickname;

    private String email;

    private String title;

    private String content;

    private String rcate1;

    private String rcate2;

    private String rcate3;

    private String longitude;

    private String latitude;

    private Long feedLikeCnt;

    private Long feedCommentCnt;

    private List<String> imagePaths;
}
