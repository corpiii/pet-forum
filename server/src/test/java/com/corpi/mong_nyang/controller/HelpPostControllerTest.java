package com.corpi.mong_nyang.controller;

import com.corpi.mong_nyang.domain.User;
import com.corpi.mong_nyang.service.HelpPostService;
import com.corpi.mong_nyang.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class HelpPostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HelpPostService helpPostService;

    @Autowired
    private UserService userService;

    User postWriter;

    @BeforeEach
    public void setup() {
        String testerEmail1 = "a@b.com";
        userService.join("test1", testerEmail1, "1111");
        postWriter = userService.findOne(testerEmail1);

        helpPostService.createPost("title1", "content", postWriter, new ArrayList<>());
        helpPostService.createPost("title2", "content", postWriter, new ArrayList<>());
    }

    @Test
    void testHelpPostList() throws Exception {
        mockMvc.perform(get("/api/helpPostList?page=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.posts[0].title").value("title1"))
                .andExpect(jsonPath("$.posts[1].title").value("title2"));
    }
}
