package com.rocket.user.user.entity;

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
@Table(name = "user_login_history")
public class UserLoginHistory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_login_history_id")
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // FK

    private LocalDateTime loginAt; // 로그인 날짜
    private String clientIp; // 아이피
    private String userAgent; // 접속환경

}
