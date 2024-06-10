package com.corpi.mong_nyang.utils;

import com.corpi.mong_nyang.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@PropertySource("classpath:jwtkey.properties")
public class JwtTokenUtil {
    private static final long ACCESS_EXPIRATION_TIME = Duration.ofMinutes(30).toMillis();
    private static final long REFRESH_EXPIRATION_TIME = Duration.ofDays(7).toMillis();

    @Value("${jwtkey}")
    private String key;

    public String generateToken(User user) {
        long current = System.currentTimeMillis();
        SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .header()
                    .add("type", "jwt")
                    .and()
                .claims()
                    .add("email", user.getEmail())
                    .issuedAt(new Date(current))
                    .expiration(new Date(current + ACCESS_EXPIRATION_TIME))
                    .and()
                .signWith(secretKey)
                .compact();
    }

    public Map<String, String> decodeToken(String token) {
        SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));

        Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);

        Claims payload = claimsJws.getPayload();
        Map<String, String> map = new HashMap<>();
        map.put("email", payload.get("email", String.class));

        return map;
    }
}
