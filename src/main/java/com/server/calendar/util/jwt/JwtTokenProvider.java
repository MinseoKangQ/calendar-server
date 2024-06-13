package com.server.calendar.util.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenProvider {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.algorithm}")
    private String algorithm;

    // 토큰 생성
    public String createToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", "USER");
        Date now = new Date();
        Date validity = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000L); // 7일 유효

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.forName(algorithm), secretKey)
                .compact();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
            return true;
        } catch (JwtException e) {
            System.out.println("Jwt Exception: " + e.getLocalizedMessage());
            return false;
        }
    }

    // 토큰에서 Claims 객체 추출
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

}
