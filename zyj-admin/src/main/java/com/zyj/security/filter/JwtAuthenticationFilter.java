//package com.zyj.security.filter;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.jsonwebtoken.security.Keys;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
//import org.springframework.util.StringUtils;
//
//import javax.crypto.SecretKey;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * JWT 认证过滤器
// * 负责从请求中提取JWT令牌，验证令牌的有效性，并设置Spring Security上下文
// *
// * @author zyj
// * @version 1.0.0
// */
//public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
//
//    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
//
//    private static final String AUTHORIZATION_HEADER = "Authorization";
//    private static final String BEARER_PREFIX = "Bearer ";
//    private static final String JWT_ALGORITHM = "RS256";
//
//    private final SecretKey secretKey;
//    private final String header;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    public JwtAuthenticationFilter(String secretKey, String header) {
//        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
//        this.header = header;
//    }
//
//    @Override
//    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//
//        try {
//            // 提取JWT令牌
//            String token = extractTokenFromRequest(request);
//
//            if (token != null) {
//                // 验证并解析JWT令牌
//                JwtPayload payload = validateAndParseToken(token);
//
//                if (payload != null) {
//                    // 创建认证对象并设置到Security上下文
//                    Authentication authentication = createAuthentication(payload);
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//
//                    logger.debug("JWT authentication successful for user: {}", payload.getSubject());
//                }
//            }
//
//            // 继续过滤器链
//            chain.doFilter(request, response);
//
//        } catch (JwtAuthenticationException e) {
//            logger.warn("JWT authentication failed: {}", e.getMessage());
//            handleAuthenticationFailure(response, e);
//        } catch (Exception e) {
//            logger.error("Unexpected error during JWT authentication", e);
//            handleAuthenticationFailure(response, new JwtAuthenticationException("Authentication error"));
//        }
//    }
//
//    /**
//     * 从请求中提取JWT令牌
//     */
//    private String extractTokenFromRequest(HttpServletRequest request) {
//        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
//
//        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
//            return authHeader.substring(BEARER_PREFIX.length());
//        }
//
//        return null;
//    }
//
//
//
//
//}
