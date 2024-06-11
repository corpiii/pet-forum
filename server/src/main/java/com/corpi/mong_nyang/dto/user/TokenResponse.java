package com.corpi.mong_nyang.dto.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class TokenResponse {
    private final String accessToken;
}
