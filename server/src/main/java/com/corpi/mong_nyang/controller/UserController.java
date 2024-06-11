package com.corpi.mong_nyang.controller;

import com.corpi.mong_nyang.domain.User;
import com.corpi.mong_nyang.dto.user.*;
import com.corpi.mong_nyang.service.UserService;
import com.corpi.mong_nyang.utils.JwtTokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<String> joinUser(@RequestBody UserCreateRequest request) {
        try {
            Long userId = userService.join(request.getName(), request.getEmail(), request.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body("User created with ID: " + userId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest userLoginRequest) throws JsonProcessingException {
        String email = userLoginRequest.getEmail();
        String password = userLoginRequest.getPassword();
        User user = userService.login(email, password);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("이메일 비밀번호를 확인해주세요"));
        }

        String accessToken = jwtTokenUtil.generateAccessToken(user);
        String refreshToken = jwtTokenUtil.generateRefreshToken(user);

        return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String refreshToken) throws JsonProcessingException {
        boolean isValidToken = jwtTokenUtil.isValidToken(refreshToken, true);

        if (!isValidToken) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Refresh 토큰이 유효하지 않습니다.");
        }

        String userEmail = jwtTokenUtil.getUserEmail(refreshToken);
        User user = userService.findOne(userEmail);

        String newAccessToken = jwtTokenUtil.generateAccessToken(user);

        return ResponseEntity.ok(new TokenResponse(newAccessToken, refreshToken));
    }

    // 유저 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestHeader("Authorization") String token) throws JsonProcessingException {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        boolean isValid = jwtTokenUtil.isValidToken(token, false);

        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Access 토큰이 유효하지 않습니다.");
        }

        String email = jwtTokenUtil.getUserEmail(token);
        User user = userService.findOne(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("유저가 존재하지 않습니다.");
        }

        userService.delete(user.getId());

        return ResponseEntity.ok("");
    }
}