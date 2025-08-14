package com.zyj.security;

import com.zyj.security.handler.AuthenticationFailureHandlerImpl;
import com.zyj.security.handler.AuthenticationSuccessHandlerImpl;
import com.zyj.security.handler.LogoutSuccessHandlerImpl;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置
 *
 * @author zyj
 */
@Configuration
public class SecurityConfig {

    private final Logger log = LoggerFactory.getLogger(getClass());


    @Resource
    AuthenticationSuccessHandlerImpl successHandler;
    @Resource
    AuthenticationFailureHandlerImpl failureHandler;
    @Resource
    LogoutSuccessHandlerImpl logoutSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                // 禁用 csrf，因为不使用session
                .csrf(AbstractHttpConfigurer::disable)
                // 过滤请求
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/sys/user1/**")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                // 登录处理
                // UsernamePasswordAuthenticationFilter 中定义了路径和参数
                .formLogin(form -> form
                        .loginPage("/login").permitAll()
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                )
                .logout(logout  -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandler)
                        .permitAll()
                )
                // 认证过滤器
//                .addFilterBefore(new JwtAuthenticationFilter(s), BasicAuthenticationFilter.class)

                // 认证失败处理
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            log.debug("请求URL：{}，403无权限", request.getRequestURL());
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                        })
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.debug("请求URL：{}，401未登录", request.getRequestURL());
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        })
                )
                // session 创建策略
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        // @formatter:on
        return http.build();
    }

    @Bean
    UserDetailsService userDetailsService() {
        // 1.使用内存数据进行认证
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        // 2.创建两个用户
        UserDetails user1 = User.withUsername("baizhan").password("123").authorities("admin").build();
        UserDetails user2 = User.withUsername("sxt").password("456").authorities("admin").build();
        // 3.将这两个用户添加到内存中
        manager.createUser(user1);
        manager.createUser(user2);
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

