package com.corpi.mong_nyang.service;

import com.corpi.mong_nyang.domain.User;
import com.corpi.mong_nyang.domain.help.HelpPosts;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class HelpPostServiceTest {
    @Autowired HelpPostService helpPostService;
    @Autowired UserService userService;

    @Test
    @DisplayName("게시글 작성 성공")
    @Commit
    public void createPost() {
        // given
        String testName = "abc";
        String testEmail = "a@b.com";
        String testPassword = "1111";

        userService.join(testName, testEmail, testPassword);

        User foundedUser = userService.findOne(testEmail);

        String testTitle = "testTitle";
        String testContent = "testContent";

        // when, then
        assertDoesNotThrow(() -> {
            helpPostService.createPost(testTitle, testContent, foundedUser);
        });
    }

    @Test
    @DisplayName("첫 번째 페이지에 있는 게시글을 fetch 성공")
    public void fetchAllPostInFirstPage() {
        // given
        String testName = "abc";
        String testEmail = "a@bc.com";
        String testPassword = "1111";

        userService.join(testName, testEmail, testPassword);

        User foundedUser = userService.findOne(testEmail);

        String testTitle = "testTitle";
        String testContent = "testContent";

        assertDoesNotThrow(() -> {
            helpPostService.createPost(testTitle, testContent, foundedUser);
        });

        // when
        List<HelpPosts> helpPosts = helpPostService.fetchPostList(0);

        assertEquals(helpPosts.get(0).getTitle(), testTitle);
    }

    @Test
    @DisplayName("post 업데이트 후 업데이트가 잘 되었는지 확인")
    public void updatePost() {
        // given
        String testName = "abc";
        String testEmail = "a@bc.com";
        String testPassword = "1111";

        userService.join(testName, testEmail, testPassword);

        User foundedUser = userService.findOne(testEmail);

        String testTitle = "testTitle";
        String testContent = "testContent";
        String newTitle = "updatedTitle";
        String newContent = "updatedContent";

        Long postId = helpPostService.createPost(testTitle, testContent, foundedUser);

        // when
        helpPostService.updatePost(postId, newTitle, newContent);

        // then
        Optional<HelpPosts> updatedPostOpt = helpPostService.findById(postId);
        assertTrue(updatedPostOpt.isPresent());

        HelpPosts updatedPost = updatedPostOpt.get();
        assertEquals(newTitle, updatedPost.getTitle());
        assertEquals(newContent, updatedPost.getContent());
    }

    @Test
    @DisplayName("post 삭제 후 삭제가 성공했는지 확인")
    public void deletePost() {
        // given
        String testName = "abc";
        String testEmail = "a@bc.com";
        String testPassword = "1111";

        userService.join(testName, testEmail, testPassword);

        User foundedUser = userService.findOne(testEmail);

        String testTitle = "testTitle";
        String testContent = "testContent";

        Long postId = helpPostService.createPost(testTitle, testContent, foundedUser);

        // when
        helpPostService.deletePost(postId);

        // then
        Optional<HelpPosts> deletedPostOpt = helpPostService.findById(postId);
        assertFalse(deletedPostOpt.isPresent());
    }
}