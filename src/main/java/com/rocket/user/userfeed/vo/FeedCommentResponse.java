package com.rocket.user.userfeed.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(Include.NON_NULL)
public class FeedCommentResponse {

    private String feedCommentId;

    private String userId;

    private String feedId;

    private String profileImagePath;

    private String userName;

    private String email;

    private String comment;

    private Long feedCommentLikeCnt;

    private boolean isLikeFeedComment;

}
