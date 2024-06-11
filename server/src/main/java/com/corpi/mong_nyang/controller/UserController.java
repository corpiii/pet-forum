package com.corpi.mong_nyang.controller;

import com.corpi.mong_nyang.domain.User;
import com.corpi.mong_nyang.dto.user.ErrorResponse;
import com.corpi.mong_nyang.dto.user.TokenResponse;
import com.corpi.mong_nyang.dto.user.UserCreateRequest;
import com.corpi.mong_nyang.dto.user.UserLoginRequest;
import com.corpi.mong_nyang.service.UserService;
import com.corpi.mong_nyang.utils.JwtTokenUtil;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

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
    public ResponseEntity<?> login(@RequestBody UserLoginRequest userLoginRequest) {
        String email = userLoginRequest.getEmail();
        String password = userLoginRequest.getPassword();
        User user = userService.login(email, password);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("이메일 비밀번호를 확인해주세요"));
        }

        String accessToken = jwtTokenUtil.generateToken(user);

        return ResponseEntity.ok(new TokenResponse(accessToken));
    }


}
