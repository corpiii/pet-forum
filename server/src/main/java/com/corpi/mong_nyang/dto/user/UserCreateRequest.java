package com.corpi.mong_nyang.dto.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserCreateRequest {
    private final String name;
    private final String email;
    private final String password;
}
