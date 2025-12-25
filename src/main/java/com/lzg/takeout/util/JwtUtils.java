package com.lzg.takeout.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    /** 建议换成自己的 256bit 随机密钥 */
    private static final String SECRET = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000L; // 24h

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public List<String> getRoles(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
        Object rolesObj = claims.get("roles");
        if (rolesObj == null) return List.of();
        String rolesStr = String.valueOf(rolesObj);
        if (rolesStr.isEmpty()) return List.of();
        return Arrays.stream(rolesStr.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
