package com.why.taskmanager.congfig;

import com.why.taskmanager.security.JwtAuthenticationFilter;
import com.why.taskmanager.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class JwtConfig {
    @Autowired
    private JwtTokenProvider jwtUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 修改以下行：添加所有 Swagger 需要的路径
                .antMatchers(
                        "/api/users/register",
                        "/api/users/login",
                        "/swagger-ui/**",
                        "/swagger-ui.html", // 添加主页面
                        "/v3/api-docs/**",  // 添加 OpenAPI 文档路径
                        "/swagger-resources/**", // 添加资源路径
                        "/webjars/**"       // 添加 WebJars 资源
                ).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}