package com.rocket.user.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class UserMypageDto {

    private Long userId;
    private String username;
    private String email;
    private String nickname;
    private Long follower;
    private Long following;

    @QueryProjection
    public UserMypageDto(Long userId, String username, String email, String nickname, Long follower, Long following) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.nickname = nickname;
        this.follower = follower;
        this.following = following;
    }
}
