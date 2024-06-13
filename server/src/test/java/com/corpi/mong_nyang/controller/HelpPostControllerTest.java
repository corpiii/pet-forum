package com.corpi.mong_nyang.controller;

import com.corpi.mong_nyang.domain.User;
import com.corpi.mong_nyang.domain.help.HelpPostComments;
import com.corpi.mong_nyang.domain.help.HelpPosts;
import com.corpi.mong_nyang.dto.help_post.HelpPostCommentDTO;
import com.corpi.mong_nyang.service.HelpPostService;
import com.corpi.mong_nyang.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    @DisplayName("PostList 페이지네이션 요청 시 정상 응답 테스트")
    void testHelpPostList() throws Exception {
        mockMvc.perform(get("/api/help-post/list?page=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.posts[0].title").value("title1"))
                .andExpect(jsonPath("$.posts[1].title").value("title2"));
    }

    @Test
    @DisplayName("포스트 조회 시 정상 조회 테스트")
    public void fetchHelpPost() throws Exception {
        /* given */
        String testTitle = "testTitle";
        String testContent = "testContent";
        String commentTestUserEmail = "comment@writer.com";

        // 포스트 작성
        Long postId = helpPostService.createPost(testTitle, testContent, postWriter, new ArrayList<>());
        HelpPosts post = helpPostService.findById(postId).get();

        // 댓글 작성
        Long userId = userService.join("댓글 작성자", commentTestUserEmail, "1111");
        User commentWriter = userService.findOne(commentTestUserEmail);
        HelpPostComments comment = HelpPostComments.of("댓글", commentWriter);

        /* when */
        post.replyComment(comment);
        comment.replyComment("대댓글", commentWriter);

        /* then */
        HelpPostCommentDTO commentDTO = HelpPostCommentDTO.from(comment);

        MvcResult mvcResult = mockMvc.perform(get("/api/help-post/{postId}", postId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments").value(commentDTO))
                .andReturn();

        System.out.println("data = " + mvcResult.getResponse().getContentAsString(Charset.defaultCharset()));
    }
}
