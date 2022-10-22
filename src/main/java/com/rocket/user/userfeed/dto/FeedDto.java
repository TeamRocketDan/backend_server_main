package com.rocket.user.userfeed.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class FeedDto {

    private String title; // 제목

    private String content; // 내용

    private String rcate1; // 지역 1 Depth, 시도 단위

    private String rcate2; // 지역 2 Depth, 구 단위

    private String rcate3; // 지역 3 Depth, 동 단위

    private String longitude; // 경도

    private String latitude; // 위도

}
