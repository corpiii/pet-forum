package com.corpi.mong_nyang.controller;

import com.corpi.mong_nyang.domain.User;
import com.corpi.mong_nyang.domain.help.HelpPostComments;
import com.corpi.mong_nyang.domain.help.HelpPosts;
import com.corpi.mong_nyang.dto.help_post.HelpPostCommentDTO;
import com.corpi.mong_nyang.service.HelpPostService;
import com.corpi.mong_nyang.service.UserService;
import com.corpi.mong_nyang.utils.JwtTokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
@Slf4j
public class HelpPostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HelpPostService helpPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

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
//                .andExpect(jsonPath("$.comments").value(commentDTO))
                .andReturn();

        String request = mvcResult.getRequest().getContentAsString();

        System.out.println("data = " + mvcResult.getResponse().getContentAsString(Charset.defaultCharset()));
    }

    @Test
    @DisplayName("포스트에 이미지 추가 업데이트 테스트")
    public void updateHelpPost() throws Exception {
        /* given */
        String testTitle = "testTitle";
        String testContent = "testContent";
        String commentTestUserEmail = "comment@writer.com";
        String accessToken = jwtTokenUtil.generateAccessToken(postWriter);

        // 유저 생성
        Long userId = userService.join("댓글 작성자", commentTestUserEmail, "1111");
        User commentWriter = userService.findOne(commentTestUserEmail);

        // 포스트 작성
        Long postId = helpPostService.createPost(testTitle, testContent, postWriter, new ArrayList<>());
        HelpPosts post = helpPostService.findById(postId).get();

        // 댓글 작성
        HelpPostComments comment = HelpPostComments.of("댓글", commentWriter);

        post.replyComment(comment);
        comment.replyComment("대댓글", commentWriter);

        /* when */

        // 이미지 파일 하나
        Path imagePath = Paths.get("/Users/ijeongmin/uploads/test/testImage.png");
        byte[] imageBytes = Files.readAllBytes(imagePath);
        MockMultipartFile imageFile = new MockMultipartFile("images", "testImage.png", MediaType.IMAGE_PNG_VALUE, imageBytes);

        // 다른 폼 데이터 준비
        String title = "Sample Title";
        String content = "Sample Content";

        MvcResult mvcResult = mockMvc.perform(multipart("/api/help-post/{postId}", postId.toString())
                        .file(imageFile)
                        .header("Authorization", "Bearer " + accessToken)
                        .param("title", title)
                        .param("content", content)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                )
                .andExpect(status().isOk())
                .andReturn();

        HelpPosts post2 = helpPostService.findById(postId).get();
        Assertions.assertEquals(1, post2.getImages().size());
    }

    @Test
    @DisplayName("글을 작성한 유저가 아닐 경우 업데이트 실패")
    public void updateFail() throws Exception {
        /* given */
        String testTitle = "testTitle";
        String testContent = "testContent";
        String anotherTestUserEmail = "comment@writer.com";

        // 유저 생성
        Long userId = userService.join("댓글 작성자", anotherTestUserEmail, "1111");
        User anotherUser = userService.findOne(anotherTestUserEmail);

        // 글을 쓴 유저가 아닌 토큰
        String accessToken = jwtTokenUtil.generateAccessToken(anotherUser);

        // 포스트 작성
        Long postId = helpPostService.createPost(testTitle, testContent, postWriter, new ArrayList<>());
        HelpPosts post = helpPostService.findById(postId).get();

        /* when */
        Path imagePath = Paths.get("/Users/ijeongmin/uploads/test/testImage.png");
        byte[] imageBytes = Files.readAllBytes(imagePath);
        MockMultipartFile imageFile = new MockMultipartFile("images", "testImage.png", MediaType.IMAGE_PNG_VALUE, imageBytes);
        String title = "Sample Title";
        String content = "Sample Content";

        mockMvc.perform(multipart("/api/help-post/{postId}", postId.toString())
                        .file(imageFile)
                        .header("Authorization", "Bearer " + accessToken)
                        .param("title", title)
                        .param("content", content)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                )
        /* then */
                .andExpect(status().isUnauthorized());
    }
}
