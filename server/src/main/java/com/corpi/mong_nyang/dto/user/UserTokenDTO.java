package com.corpi.mong_nyang.dto.user;

import com.corpi.mong_nyang.domain.User;
import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class UserTokenDTO {
    @NonNull
    private String email;

    @NonNull
    private String password;

    @NonNull
    private String name;

    @NonNull
    private boolean isRefresh;

    @NonNull
    Date expiredAt;

    public static UserTokenDTO from(User user, boolean isRefresh, Date expiredAt) {
        return new UserTokenDTO(user.getEmail(), user.getPassword(), user.getName(), isRefresh, expiredAt);
    }
}