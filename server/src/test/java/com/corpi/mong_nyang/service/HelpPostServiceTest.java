package com.corpi.mong_nyang.service;

import com.corpi.mong_nyang.domain.User;
import com.corpi.mong_nyang.domain.help.HelpPostComments;
import com.corpi.mong_nyang.domain.help.HelpPosts;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}