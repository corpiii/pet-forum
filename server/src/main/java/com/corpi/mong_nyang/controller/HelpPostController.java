package com.corpi.mong_nyang.controller;

import com.corpi.mong_nyang.domain.User;
import com.corpi.mong_nyang.domain.help.HelpPostImages;
import com.corpi.mong_nyang.domain.help.HelpPosts;
import com.corpi.mong_nyang.dto.help_post.*;
import com.corpi.mong_nyang.service.HelpPostService;
import com.corpi.mong_nyang.service.ImageStoreService;
import com.corpi.mong_nyang.service.UserService;
import com.corpi.mong_nyang.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/help-post")
@RequiredArgsConstructor
public class HelpPostController {
    private final HelpPostService helpPostService;
    private final JwtTokenUtil jwtTokenUtil;
    private final ImageStoreService imageStoreService;
    private final UserService userService;

    @GetMapping("/list")
    public HelpPostListResponse helpPostList(@RequestParam(defaultValue = "0") int page) {
        List<HelpPosts> helpPosts = helpPostService.fetchPostList(page);
        List<HelpPostSummaryDTO> postSummaries = helpPosts.stream()
                .map(post -> new HelpPostSummaryDTO(post.getId(), post.getTitle()))
                .toList();

        return new HelpPostListResponse(postSummaries.size(), postSummaries);
    }

    @PostMapping("/publish")
    public ResponseEntity<?> publishPost(@RequestHeader("Authorization") String token, @ModelAttribute HelpPostRequest helpPostRequest) throws IOException {
        String email = jwtTokenUtil.getUserEmail(token);
        User user = userService.findOne(email);

        if (!jwtTokenUtil.isValidToken(token, false) || user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Access 토큰이 유효하지 않습니다.");
        }

        if (helpPostRequest.getImages().size() > 5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미지는 5장까지 가능합니다.");
        }

        List<HelpPostImages> images = new ArrayList<>();

        for (MultipartFile file : helpPostRequest.getImages()) {
            String path = imageStoreService.saveImage(file);

            images.add(HelpPostImages.of(path));
        }

        helpPostService.createPost(helpPostRequest.getTitle(), helpPostRequest.getContent(), user, images);

        return ResponseEntity.status(HttpStatus.CREATED).body("포스트가 성공적으로 생성되었습니다.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> fetchOne(@PathVariable long id) {
        Optional<HelpPosts> foundedPost = helpPostService.findById(id);

        if (foundedPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("포스트가 존재하지 않습니다.");
        }

        HelpPosts post = foundedPost.get();

        return ResponseEntity.ok().body(HelpPostResponse.from(post));
    }
}