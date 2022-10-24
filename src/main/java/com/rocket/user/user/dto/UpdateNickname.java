package com.rocket.user.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNickname {

    @NotBlank(message = "닉네임은 필수 입력 사항입니다.")
    private String nickname;
}
