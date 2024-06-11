package com.corpi.mong_nyang.utils;

import com.corpi.mong_nyang.domain.User;
import com.corpi.mong_nyang.dto.user.UserTokenDTO;
import com.corpi.mong_nyang.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class JwtTokenUtilTest {
    @Autowired JwtTokenUtil jwtTokenUtil;

    @Autowired UserService userService;

    @Test
    @DisplayName("유저 jwt 생성 후 decode 테스트")
    void generateToken() throws JsonProcessingException {
        // given
        String name = "testName";
        String email = "testEmail";
        String password = "testPassword";

        userService.join(name, email, password);
        User user = userService.findOne(email);

        String jwt = jwtTokenUtil.generateAccessToken(user);

        log.info("jwt: " + jwt);

        // when
        UserTokenDTO userTokenDTO = jwtTokenUtil.decodeToken(jwt);

        // then
        assertEquals(user.getEmail(), userTokenDTO.getEmail());
        assertEquals(user.getPassword(), userTokenDTO.getPassword());
        assertEquals(user.getName(), userTokenDTO.getName());

    }
}