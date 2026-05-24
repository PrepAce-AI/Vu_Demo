package com.vu.config;

import com.vu.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. Dùng final và không dùng @Autowired ở đây
    private final CustomOAuth2UserService oauthUserService;

    // 2. Tiêm qua Constructor (Đây là cách Spring khuyến khích nhất)
    public SecurityConfig(CustomOAuth2UserService oauthUserService) {
        this.oauthUserService = oauthUserService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register", "/login", "/forgot-password", "/reset-password", "/css/**", "/js/**", "/uploads/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oauthUserService) // Spring sẽ lấy cái đã tiêm vào qua constructor
                        )
                        .defaultSuccessUrl("/profile", true)
                )
                .logout(logout -> logout.permitAll());

        return http.build();
    }
}