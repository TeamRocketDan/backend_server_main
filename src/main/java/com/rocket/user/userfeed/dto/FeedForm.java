package com.rocket.user.userfeed.dto;

import com.rocket.user.user.entity.User;
import javax.persistence.Lob;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedForm {
    //피드폼 필요없을지도

    private User user; // FK

    @Size(max = 100)
    @NotEmpty(message = "피드에 제목을 작성해 주세요.")
    private String title; // 제목

    @Lob
    @NotEmpty(message = "피드에 내용을 작성해 주세요.")
    private String content; // 내용

    @Size(max = 20)
    @NotEmpty(message = "특별시 / 광역시를 선택해 주세요.")
    private String rcate1; // 지역 1 Depth, 시도 단위

    @Size(max = 20)
    @NotEmpty(message = "시 / 군 / 구 를 선택해 주세요.")
    private String rcate2; // 지역 2 Depth, 구 단위

    @Size(max = 20)
    private String rcate3; // 지역 3 Depth, 동 단위

    @Size(max = 50)
    @NotEmpty(message = "경도 값이 누락되었습니다.")
    private String longitude; // 경도

    @Size(max = 50)
    @NotEmpty(message = "위도 값이 누락되었습니다.")
    private String latitude; // 위도

}
