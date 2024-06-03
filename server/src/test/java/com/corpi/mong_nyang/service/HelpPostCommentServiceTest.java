package com.corpi.mong_nyang.service;

import com.corpi.mong_nyang.domain.User;
import com.corpi.mong_nyang.domain.help.HelpPosts;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class HelpPostCommentServiceTest {
    @Autowired
    HelpPostCommentService helpPostCommentService;
    @Autowired
    HelpPostService helpPostService;
    @Autowired
    UserService userService;

    User postWriter;
    User commentWriter;

    @BeforeEach
    public void setup() {
        String testerEmail1 = "a@b.com";
        String testerEmail2 = "a@c.com";

        userService.join("test1", testerEmail1, "1111");
        userService.join("test2", testerEmail2, "1111");

        postWriter = userService.findOne(testerEmail1);
        commentWriter = userService.findOne(testerEmail2);
    }

    @Test
    @DisplayName("게시글에 댓글을 달았을 때 정상적으로 달아지는지 확인")
    public void commentToPost() {
        // given
        String postTitle = "Post Title";
        String postContent = "Post content";
        String commentContent = "Test Comment";

        helpPostService.createPost(postTitle, postContent, postWriter);

        List<HelpPosts> helpPosts = helpPostService.fetchPostList(0);

        assertEquals(helpPosts.size(), 1);
        assertEquals(helpPosts.get(0).getTitle(), postTitle);
        assertEquals(helpPosts.get(0).getContent(), postContent);

        HelpPosts createdPost = helpPosts.get(0);

        // when
        helpPostCommentService.commentByUser(createdPost.getId(), commentWriter.getId(), commentContent);

        // then
        HelpPosts commentedPost = helpPostService.findById(helpPosts.get(0).getId()).get();

        assertEquals(commentedPost.getComments().size(), 1);
        assertEquals(commentedPost.getComments().get(0).getAuthor(), commentWriter);
        assertEquals(commentedPost.getComments().get(0).getContent(), commentContent);
    }
}