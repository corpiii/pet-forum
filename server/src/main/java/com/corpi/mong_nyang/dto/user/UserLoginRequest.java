package com.corpi.mong_nyang.dto.user;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String email;
    private String password;
}
