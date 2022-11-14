package com.rocket.user.userfeed.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeedSearchCondition {

    @Size(max = 20)
//    @NotEmpty(message = "특별시 / 광역시를 선택해 주세요.")
    private String rcate1;

    @Size(max = 20)
//    @NotEmpty(message = "시 / 군 / 구 를 선택해 주세요.")
    private String rcate2;

}
