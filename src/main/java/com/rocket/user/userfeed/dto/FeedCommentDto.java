package com.rocket.user.userfeed.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.rocket.user.userfeed.entity.FeedComment;
import javax.persistence.Lob;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class FeedCommentDto {

    Long feedCommentId;

    @Lob
    @NotEmpty(message = "피드에 댓글을 남겨주세요.")
    private String comment;

    public static FeedCommentDto of(FeedComment feedComment) {
        return FeedCommentDto.builder()
            .comment(feedComment.getComment())
            .build();
    }
}
