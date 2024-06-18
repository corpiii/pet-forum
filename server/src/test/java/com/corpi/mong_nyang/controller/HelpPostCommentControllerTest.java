package com.corpi.mong_nyang.controller;

import com.corpi.mong_nyang.domain.User;
import com.corpi.mong_nyang.domain.help.HelpPosts;
import com.corpi.mong_nyang.service.HelpPostService;
import com.corpi.mong_nyang.service.UserService;
import com.corpi.mong_nyang.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Slf4j
class HelpPostCommentControllerTest {
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    HelpPostService helpPostService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserService userService;

    User postWriter;

    @BeforeEach
    public void setUp() {
        String name = "포스트 생성자";
        String email = "postCommentTest@email.com";
        String password = "1234";

        userService.join(name, email, password);

        postWriter = userService.findOne(email);
    }

    @Test
    @DisplayName("포스트 댓글 달기 정상 실행 테스트")
    public void deleteHelpPost() throws Exception {
        /* given */
        String testTitle = "testTitle";
        String testContent = "testContent";
        String accessToken = jwtTokenUtil.generateAccessToken(postWriter);

        // 포스트 작성
        Long postId = helpPostService.createPost(testTitle, testContent, postWriter, new ArrayList<>());
        HelpPosts post = helpPostService.findById(postId).get();

        /* when */
        MvcResult mvcResult = mockMvc.perform(post("/api/help-post-comment/reply/{postId}", postId.toString())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"새로운 댓글\"}")
                )
        /* then */
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertEquals(1, post.getComments().size());
    }
}