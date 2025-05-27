package com.raehyeon.vroom.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateMemberRequest {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "회원 가입 시 이메일 입력은 필수 사항입니다.")
    private String email;

    @Size(min = 8)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", message = "비밀번호는 8자 이상이며, 문자, 숫자, 특수문자를 포함해야 합니다.")
    @NotBlank(message = "회원 가입 시 비밀번호 입력은 필수 사항입니다.")
    private String password;

    @Size(min = 2, max = 10, message = "닉네임은 최소 2자, 최대 10자까지 입력 가능합니다.")
    @NotBlank(message = "회원 가입 시 닉네임 입력은 필수 사항입니다.")
    private String nickname;

}
