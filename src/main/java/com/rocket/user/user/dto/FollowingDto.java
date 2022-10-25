package com.rocket.user.user.dto;

import com.rocket.user.user.entity.Follow;
import lombok.*;
import org.springframework.util.StringUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowingDto {

    private Long userId;
    private String profileImagePath;
    private String nickname;
    private String email;

    public static FollowingDto fromEntity(Follow follow) {
        return FollowingDto.builder()
                .userId(follow.getFollower().getId())
                .profileImagePath(follow.getFollower().getProfileImage())
                .nickname(
                        StringUtils.hasText(follow.getFollower().getNickname()) ?
                                follow.getFollower().getNickname()
                                :
                                follow.getFollower().getUsername()
                )
                .email(follow.getFollower().getEmail())
                .build();
    }
}
