package com.rocket.user.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_refresh_token")
public class UserRefreshToken {

    @Id
    private String uuid; // 소셜 아이디
    private String refreshToken; // 리프레쉬 토큰

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}