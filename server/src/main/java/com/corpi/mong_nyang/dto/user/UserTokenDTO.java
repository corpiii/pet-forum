package com.corpi.mong_nyang.dto.user;

import com.corpi.mong_nyang.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTokenDTO {
    private String email;
    private String password;
    private String name;

    public static UserTokenDTO from(User user) {
        return new UserTokenDTO(user.getEmail(), user.getPassword(), user.getName());
    }
}