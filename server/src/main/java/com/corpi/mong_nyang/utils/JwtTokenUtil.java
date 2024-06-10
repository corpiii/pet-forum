package com.corpi.mong_nyang.utils;

import com.corpi.mong_nyang.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTokenUtil {
    private static final SecretKey key = Jwts.SIG.HS256.key().build();

    public static String generateToken(User user, long expireTime) {
        long current = System.currentTimeMillis();

        return Jwts.builder()
                .header()
                    .add("type", "jwt")
                    .and()
                .claims()
                    .add("email", user.getEmail())
                    .issuedAt(new Date(current))
                    .expiration(new Date(current + expireTime))
                    .and()
                .signWith(key)
                .compact();
    }

    public static Map<String, String> decodeToken(String token) {
        Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);

        Claims payload = claimsJws.getPayload();
        Map<String, String> map = new HashMap<>();
        map.put("email", payload.get("email", String.class));

        return map;
    }
}
