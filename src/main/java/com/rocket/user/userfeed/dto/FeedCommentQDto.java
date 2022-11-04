package com.rocket.user.userfeed.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class FeedCommentQDto {

    private Long userId;
    private String profileImagePath;
    private String nickname;
    private String username;
    private String email;
    private Long commentId;
    private Long feedId;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long commentLikeCnt;
    private boolean isLikeFeedComment;

    @QueryProjection
    public FeedCommentQDto(Long userId, String profileImagePath, String nickname, String username, String email, Long commentId, Long feedId, String comment, LocalDateTime createdAt, LocalDateTime updatedAt, Long commentLikeCnt, boolean isLikeFeedComment) {
        this.userId = userId;
        this.profileImagePath = profileImagePath;
        this.nickname = nickname;
        this.username = username;
        this.email = email;
        this.commentId = commentId;
        this.feedId = feedId;
        this.comment = comment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.commentLikeCnt = commentLikeCnt;
        this.isLikeFeedComment = isLikeFeedComment;
    }
}
