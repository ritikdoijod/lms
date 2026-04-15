package com.lms.authservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private final SecretKey key = Jwts.SIG.HS256.key().build();

    public String generateAccessToken(Authentication auth) {
        String roles = populateAuthorities(auth);
        return Jwts
                .builder()
                .subject(auth.getName())
                .claim("authorities", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(key).compact();
    }

//    public String generateRefreshToken(String email) {
//        return Jwts.builder().subject(email).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)).signWith(key).compact();
//    }

    private String populateAuthorities(Authentication auth) {
        return auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
    }

    public Claims extractClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public List<GrantedAuthority> extractAuthorities(String token) {
        String authorities = String.valueOf(extractClaims(token).get("authorities"));
        return AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
    }


}
