package com.corpi.mong_nyang.utils;

import com.corpi.mong_nyang.domain.User;
import com.corpi.mong_nyang.dto.user.UserTokenDTO;
import com.corpi.mong_nyang.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
@PropertySource("classpath:jwtkey.properties")
public class JwtTokenUtil {
    private static final long ACCESS_EXPIRATION_TIME = Duration.ofMinutes(30).toMillis();
    private static final long REFRESH_EXPIRATION_TIME = Duration.ofDays(1).toMillis();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${jwtkey}")
    private String key;

    public String generateAccessToken(User user) throws JsonProcessingException {
        long current = System.currentTimeMillis();
        SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        UserTokenDTO userTokenDTO = UserTokenDTO.from(user, false, new Date(current + ACCESS_EXPIRATION_TIME));
        String json = objectMapper.writeValueAsString(userTokenDTO);

        return Jwts.builder()
                .header()
                    .add("type", "jwt")
                    .and()
                .claims()
                    .add("user", json)
                    .issuedAt(new Date(current))
                    .expiration(new Date(current + ACCESS_EXPIRATION_TIME))
                    .and()
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(User user) throws JsonProcessingException {
        long current = System.currentTimeMillis();
        SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        UserTokenDTO userTokenDTO = UserTokenDTO.from(user, false, new Date(current +REFRESH_EXPIRATION_TIME));
        String json = objectMapper.writeValueAsString(userTokenDTO);

        return Jwts.builder()
                .header()
                    .add("type", "jwt")
                    .and()
                .claims()
                    .add("user", json)
                    .issuedAt(new Date(current))
                    .expiration(new Date(current + REFRESH_EXPIRATION_TIME))
                    .and()
                .signWith(secretKey)
                .compact();
    }

    public UserTokenDTO decodeToken(String token) throws JsonProcessingException {
        SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));

        Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);

        Claims payload = claimsJws.getPayload();
        String json = payload.get("user", String.class);

        return objectMapper.readValue(json, UserTokenDTO.class);
    }

    public boolean isValidToken(String token, boolean isRefresh) throws JsonProcessingException {
        if (!token.startsWith("Bearer ")) {
            return false;
        }

        token = token.substring(7);
        UserTokenDTO userTokenDTO = decodeToken(token);

        if (userTokenDTO.isRefresh() != isRefresh) {
            return false;
        }

        Date expiredAt = userTokenDTO.getExpiredAt();

        if (expiredAt.before(new Date(System.currentTimeMillis()))) {
            return false;
        }

        return true;
    }

    public String getUserEmail(String token) throws JsonProcessingException {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        UserTokenDTO userTokenDTO = decodeToken(token);

        return userTokenDTO.getEmail();
    }
}
