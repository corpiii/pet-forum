package com.corpi.mong_nyang.service;

import com.corpi.mong_nyang.domain.User;
import com.corpi.mong_nyang.domain.help.HelpPostComments;
import com.corpi.mong_nyang.domain.help.HelpPosts;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        String testerEmail1 = "tester1@bb.com";
        String testerEmail2 = "tester2@cc.com";

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

        helpPostService.createPost(postTitle, postContent, postWriter, new ArrayList<>());

        List<HelpPosts> helpPosts = helpPostService.fetchPostList(0);

        assertEquals(helpPosts.size(), 1);
        assertEquals(helpPosts.get(0).getTitle(), postTitle);
        assertEquals(helpPosts.get(0).getContent(), postContent);

        HelpPosts createdPost = helpPosts.get(0);

        // when
        HelpPostComments comment = HelpPostComments.of(commentContent, commentWriter);
        helpPostCommentService.createCommentByUserInPost(createdPost.getId(), comment);

        // then
        HelpPosts commentedPost = helpPostService.findById(helpPosts.get(0).getId()).get();

        assertEquals(commentedPost.getComments().size(), 1);
        assertEquals(commentedPost.getComments().get(0).getAuthor(), commentWriter);
        assertEquals(commentedPost.getComments().get(0).getContent(), commentContent);
    }
    
    @Test
    @DisplayName(value = "댓글을 수정 했을 때 게시글에서 잘 반영되는지 확인")
    public void updateCommentTest() {
        // given
        String postTitle = "Post Title";
        String postContent = "Post content";
        String commentContent = "Test Comment";
        String updatedCommentContent = "Updated Post Content";

        Long postId = helpPostService.createPost(postTitle, postContent, postWriter, new ArrayList<>());
        HelpPosts createdPost = helpPostService.findById(postId).get();
        
        assertEquals(createdPost.getTitle(), postTitle);
        assertEquals(createdPost.getContent(), postContent);

        HelpPostComments comment = HelpPostComments.of(commentContent, commentWriter);
        Long createdCommentId = helpPostCommentService.createCommentByUserInPost(createdPost.getId(), comment);

        assertEquals(createdPost.getComments().get(0).getContent(), commentContent);

        // when
        helpPostCommentService.updateComment(createdCommentId, updatedCommentContent);

        // then
        assertEquals(createdPost.getComments().get(0).getContent(), updatedCommentContent);
    }

    @Test
    @DisplayName("댓글을 삭제 했을 때 정상적으로 삭제되는지 확인")
    public void deleteCommentTest() {
        // given
        String postTitle = "Post Title";
        String postContent = "Post content";
        String commentContent = "Test Comment";

        Long postId = helpPostService.createPost(postTitle, postContent, postWriter, new ArrayList<>());
        HelpPosts createdPost = helpPostService.findById(postId).get();

        assertEquals(createdPost.getTitle(), postTitle);
        assertEquals(createdPost.getContent(), postContent);

        HelpPostComments comment = HelpPostComments.of(commentContent, commentWriter);
        Long createdCommentId = helpPostCommentService.createCommentByUserInPost(createdPost.getId(), comment);

        assertEquals(createdPost.getComments().get(0).getContent(), commentContent);

        // when
        helpPostCommentService.deleteComment(createdCommentId);

        // then
        HelpPostComments createdComment = createdPost.getComments().get(0);

        assertEquals(createdComment.getContent(), null);
        assertEquals(createdComment.getAuthor(), null);
    }

    @Test
    @DisplayName("대댓글을 달고 난 후 정상적으로 동작하는지 확인")
    public void replyCommentTest() {
        // given
        String postTitle = "Post Title";
        String postContent = "Post content";
        String commentContent = "Test Comment";
        String replyCommentContent = "Test Reply Comment";

        Long postId = helpPostService.createPost(postTitle, postContent, postWriter, new ArrayList<>());
        HelpPosts createdPost = helpPostService.findById(postId).get();

        assertEquals(createdPost.getTitle(), postTitle);
        assertEquals(createdPost.getContent(), postContent);

        HelpPostComments comment = HelpPostComments.of(commentContent, commentWriter);
        Long createdCommentId = helpPostCommentService.createCommentByUserInPost(createdPost.getId(), comment);

        assertEquals(createdPost.getComments().get(0).getContent(), commentContent);

        // when
        HelpPostComments willRepliedComment = helpPostCommentService.findById(createdCommentId);
        HelpPostComments reComment = HelpPostComments.of(replyCommentContent, commentWriter);

        willRepliedComment.replyComment(reComment);

        // then
        HelpPosts helpPosts = helpPostService.fetchPostList(0).get(0);

        assertEquals(helpPosts.getComments().get(0).getContent(), commentContent);
        assertEquals(helpPosts.getComments().get(0).getChildren().get(0).getContent(), replyCommentContent);
    }

    @Test
    @DisplayName("게시글을 삭제했을 때 댓글과 대댓글 모두 같이 삭제")
    public void deletePostThenCommentRemoveTest() {
        // given
        String postTitle = "Post Title";
        String postContent = "Post content";
        String commentContent = "Test Comment";
        String replyCommentContent = "Test Reply Comment";

        Long postId = helpPostService.createPost(postTitle, postContent, postWriter, new ArrayList<>());
        HelpPosts createdPost = helpPostService.findById(postId).get();

        assertEquals(createdPost.getTitle(), postTitle);
        assertEquals(createdPost.getContent(), postContent);

        HelpPostComments comment = HelpPostComments.of(commentContent, commentWriter);
        Long createdCommentId = helpPostCommentService.createCommentByUserInPost(createdPost.getId(), comment);

        assertEquals(createdPost.getComments().get(0).getContent(), commentContent);

        HelpPostComments willRepliedComment = helpPostCommentService.findById(createdCommentId);
        HelpPostComments reComment = HelpPostComments.of(replyCommentContent, commentWriter);

        willRepliedComment.replyComment(reComment);

        // when
        helpPostService.deletePost(postId);

        // then
        assertNull(helpPostCommentService.findById(willRepliedComment.getId()));
    }
}