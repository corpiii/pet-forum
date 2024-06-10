package com.corpi.mong_nyang.utils;

import com.corpi.mong_nyang.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class JwtTokenUtilTest {
    @Test
    @DisplayName("유저 jwt 생성 후 decode 테스트")
    void generateToken() {
        // given
        String name = "testName";
        String email = "testEmail";
        String password = "testPassword";

        User user = User.of(name, email, password);
        long oneMinute = 60 * 1000;

        String jwt = JwtTokenUtil.generateToken(user, oneMinute);

        log.info("jwt: " + jwt);

        // when
        Map<String, String> decodeToken = JwtTokenUtil.decodeToken(jwt);

        // then
        assertEquals(email, decodeToken.get("email"));
    }
}