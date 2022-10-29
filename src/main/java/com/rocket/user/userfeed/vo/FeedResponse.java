package com.rocket.user.userfeed.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.rocket.user.userfeed.entity.FeedComment;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

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

    private String longitude;

    private String latitude;

    private Long feedLikeCnt;

    private Long feedCommentCnt;

    private List<String> feedImages;

    private Page<FeedComment> feedComment;

    private Long commentLikeCnt;
}
