package com.corpi.mong_nyang.dto.user;

import lombok.Getter;

@Getter
public class UserLoginRequest {
    private String email;
    private String password;
}
