package com.rocket.user.userfeed.dto;

import javax.persistence.Lob;
import javax.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeedCommentDto {

    Long feedCommentId;

    @Lob
    @NotEmpty(message = "피드에 댓글을 남겨주세요.")
    private String comment;
}
