package com.corpi.mong_nyang.controller;

import com.corpi.mong_nyang.domain.User;
import com.corpi.mong_nyang.dto.user.UserLoginRequest;
import com.corpi.mong_nyang.service.HelpPostService;
import com.corpi.mong_nyang.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    User user;

    @BeforeEach
    public void setup() {
        String testerEmail1 = "a@b.com";
        userService.join("test1", testerEmail1, "1111");
        user = userService.findOne(testerEmail1);
    }

    @Test
    @DisplayName("로그인 시 토큰 발급")
    public void publishTokenWhenLogin() throws Exception {
        // given
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setEmail(user.getEmail());
        userLoginRequest.setPassword(user.getPassword());

        ObjectMapper objectMapper = new ObjectMapper();
        String loginJson = objectMapper.writeValueAsString(userLoginRequest);

        // when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }
}