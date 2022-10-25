package com.rocket.user.user.dto;

import com.rocket.user.user.entity.Follow;
import lombok.*;
import org.springframework.util.StringUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowerDto {

    private Long userId;
    private String profileImagePath;
    private String nickname;
    private String email;

    public static FollowerDto fromEntity(Follow follow) {
        return FollowerDto.builder()
                .userId(follow.getFollowing().getId())
                .profileImagePath(follow.getFollowing().getProfileImage())
                .nickname(
                        StringUtils.hasText(follow.getFollowing().getNickname()) ?
                                follow.getFollowing().getNickname()
                                :
                                follow.getFollowing().getUsername()
                )
                .email(follow.getFollowing().getEmail())
                .build();
    }
}
