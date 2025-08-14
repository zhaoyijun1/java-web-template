package com.zyj.security.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 处理 JWT 的生成、解析和验证
 *
 * <p>token格式: header.payload.signature</p>
 *
 * <div>header: {"alg":"HS256","typ":"JWT"}</div>
 * <div>payload: {"sub":"username","exp":1672531200}</div>
 * <div>signature: base64(hmac256(header.payload, secret))</div>
 *
 * @author zyj
 * @version 1.0.0
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

    /**
     * 获取密钥
     */
    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成token
     *
     * <p>Spring Security 认证成功后，生成包含用户信息的 Authentication 对象：</p>
     *
     * <div>Principal： 用户身份（用户名或用户ID）</div>
     * <div>Credentials： 用户凭证（密码）</div>
     * <div>Authorities： 用户角色权限</div>
     *
     * @param authentication 用户信息
     * @return token
     */
    public String generateToken(Authentication authentication) {

        // 获取用户名
        String username = authentication.getName();

        // 获取用户角色权限
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // 设置过期时间
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        // 生成token
        return Jwts.builder()
                .subject(username)
                .claim("authorities", authorities)
                .issuedAt(now) // 签发时间
                .expiration(expiryDate) // 过期时间
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 解析token
     */
    public Map<String, Object> parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 验证token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token", e);
            return false;
        }
    }

}
