package com.corpi.mong_nyang.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class UserServiceTest {
    @Autowired 
    UserService userService;
    
    @Test
    @DisplayName("유저 회원가입 성공 테스트")
    public void join() {
        // given
        String name = "abc";
        String email = "userJoin@b.com";
        String password = "1111";

        // when, then
        Assertions.assertDoesNotThrow(() -> {
            userService.join(name, email, password);
        });
    }

    @Test
    @DisplayName("중복 이메일로 인한 회원가입 실패")
    public void join_fail() {
        // given
        String name = "abc";
        String email = "duplicate@a.com";
        String password = "1111";

        userService.join(name, email, password);

        String name1 = "aaa";
        String email1 = "duplicate@a.com";
        String password1 = "2222";

        // when, then
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.join(name1, email1, password1);
        });
    }
}
