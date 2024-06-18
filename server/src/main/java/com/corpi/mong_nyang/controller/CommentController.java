package com.corpi.mong_nyang.controller;

import com.corpi.mong_nyang.domain.User;
import com.corpi.mong_nyang.domain.help.HelpPostComments;
import com.corpi.mong_nyang.domain.help.HelpPosts;
import com.corpi.mong_nyang.dto.help_post.HelpPostCommentRequest;
import com.corpi.mong_nyang.service.HelpPostService;
import com.corpi.mong_nyang.service.UserService;
import com.corpi.mong_nyang.utils.JwtTokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/comment")
@RequiredArgsConstructor
public class CommentController {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    HelpPostService helpPostService;

    @Autowired
    UserService userService;

    @PostMapping("/reply/{postId}")
    public ResponseEntity<?> reply(@PathVariable("postId") long id, @RequestHeader("Authorization") String token, @RequestBody HelpPostCommentRequest request) throws JsonProcessingException {
        // 토큰 확인
        if (!jwtTokenUtil.isValidToken(token, false)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access 토큰이 유효하지 않습니다.");
        }

        // 포스트 가져오기
        Optional<HelpPosts> foundedPost = helpPostService.findById(id);

        if (foundedPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("post가 존재하지 않습니다.");
        }

        HelpPosts post = foundedPost.get();

        // 댓글 달기
        String email = jwtTokenUtil.getUserEmail(token);
        User author = userService.findOne(email);
        HelpPostComments helpPostComments = HelpPostComments.of(request.getContent(), author);

        post.replyComment(helpPostComments);

        return ResponseEntity.ok("댓글이 달렸습니다.");
    }
}
