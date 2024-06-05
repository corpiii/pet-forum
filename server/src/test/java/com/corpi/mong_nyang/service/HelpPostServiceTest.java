package com.corpi.mong_nyang.service;

import com.corpi.mong_nyang.domain.User;
import com.corpi.mong_nyang.domain.help.HelpPostImages;
import com.corpi.mong_nyang.domain.help.HelpPosts;
import com.corpi.mong_nyang.repository.HelpPostImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class HelpPostServiceTest {
    @Autowired HelpPostService helpPostService;
    @Autowired UserService userService;
    @Autowired HelpPostImageRepository helpPostImageRepository;

    User testUser;

    @BeforeEach
    void setup() {
        String testName = "abc";
        String testEmail = "a@bc.com";
        String testPassword = "1111";

        userService.join(testName, testEmail, testPassword);

        testUser = userService.findOne(testEmail);
    }

    @AfterEach
    void tearDown() {
        userService.delete(testUser.getId());
    }

    @Test
    @DisplayName("게시글 작성 성공")
    @Commit
    public void createPost() {
        // given
        String testTitle = "testTitle";
        String testContent = "testContent";

        // when, then
        assertDoesNotThrow(() -> {
            helpPostService.createPost(testTitle, testContent, testUser, new ArrayList<>());
        });
    }

    @Test
    @DisplayName("첫 번째 페이지에 있는 게시글을 fetch 성공")
    public void fetchAllPostInFirstPage() {
        // given
        String testTitle = "testTitle";
        String testContent = "testContent";

        assertDoesNotThrow(() -> {
            helpPostService.createPost(testTitle, testContent, testUser, new ArrayList<>());
        });

        // when
        List<HelpPosts> helpPosts = helpPostService.fetchPostList(0);

        assertEquals(helpPosts.get(0).getTitle(), testTitle);
    }

    @Test
    @DisplayName("post 업데이트 후 업데이트가 잘 되었는지 확인")
    public void updatePost() {
        // given
        String testTitle = "testTitle";
        String testContent = "testContent";
        String newTitle = "updatedTitle";
        String newContent = "updatedContent";

        Long postId = helpPostService.createPost(testTitle, testContent, testUser, new ArrayList<>());

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
        String testTitle = "testTitle";
        String testContent = "testContent";

        Long postId = helpPostService.createPost(testTitle, testContent, testUser, new ArrayList<>());

        // when
        helpPostService.deletePost(postId);

        // then
        Optional<HelpPosts> deletedPostOpt = helpPostService.findById(postId);
        assertFalse(deletedPostOpt.isPresent());
    }

    @Test
    @DisplayName("post 업데이트 시 사진 추가 테스트")
    public void updatePostWithAddImage() {
        // given
        String testTitle = "testTitle";
        String testContent = "testContent";

        Long postId = helpPostService.createPost(testTitle, testContent, testUser, List.of());
        HelpPosts createdPost = helpPostService.findById(postId).get();

        assertEquals(createdPost.getTitle(), testTitle);
        assertEquals(createdPost.getContent(), testContent);

        HelpPostImages helpPostImage = HelpPostImages.of("testUrl");

        // when
        helpPostService.addImage(postId, List.of(helpPostImage));

        // then
        assertEquals(createdPost.getImages().get(0), helpPostImage);
        assertEquals(helpPostImageRepository.findAll().get(0).getUrl(), helpPostImage.getUrl());
    }

    @Test
    @DisplayName("post 이미지 삭제 시 정상 연관삭제 테스트")
    public void deletePostImageThenDeleteImageInDB() {
        // given
        String testTitle = "testTitle";
        String testContent = "testContent";

        Long postId = helpPostService.createPost(testTitle, testContent, testUser, List.of());
        HelpPosts createdPost = helpPostService.findById(postId).get();

        assertEquals(createdPost.getTitle(), testTitle);
        assertEquals(createdPost.getContent(), testContent);

        HelpPostImages helpPostImage = HelpPostImages.of("testUrl");

        helpPostService.addImage(postId, List.of(helpPostImage));

        // when
        helpPostService.removeImage(postId, 0);

        // then
        assertTrue(helpPostImageRepository.findAll().isEmpty());
    }

    @Test
    @DisplayName("post 삭제 시 이미지 연관 삭제 테스트")
    public void deletePostThenImageDeleteInDB() {
        // given
        String testTitle = "testTitle";
        String testContent = "testContent";

        Long postId = helpPostService.createPost(testTitle, testContent, testUser, List.of());
        HelpPosts createdPost = helpPostService.findById(postId).get();

        assertEquals(createdPost.getTitle(), testTitle);
        assertEquals(createdPost.getContent(), testContent);

        HelpPostImages helpPostImage = HelpPostImages.of("testUrl");

        helpPostService.addImage(postId, List.of(helpPostImage));

        // when
        helpPostService.deletePost(postId);

        // then
        assertEquals(helpPostImageRepository.findAll().size(), 0);
    }
}