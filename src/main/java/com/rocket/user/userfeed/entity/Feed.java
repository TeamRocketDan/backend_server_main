package com.rocket.user.userfeed.entity;

import com.rocket.config.jpa.entitiy.BaseEntity;
import com.rocket.user.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "feed")
public class Feed extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_id")
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // FK

    private String title; // 제목
    private String content; // 내용

    private String rcate1; // 지역 1 Depth, 시도 단위
    private String rcate2; // 지역 2 Depth, 구 단위
    private String rcate3; // 지역 3 Depth, 동 단위
    private String longitude; // 경도
    private String latitude; // 위도
    private LocalDateTime deletedAt; // 삭제 날짜
}
