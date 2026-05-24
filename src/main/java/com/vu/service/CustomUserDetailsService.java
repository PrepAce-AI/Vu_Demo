package com.vu.service;

import com.vu.entity.User;
import com.vu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Tìm user trong cơ sở dữ liệu dựa vào email người dùng nhập
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("Không tìm thấy tài khoản với email: " + email);
        }

        // 2. Chuyển đổi User của chúng ta thành UserDetails của Spring Security
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(), // QUAN TRỌNG: Nếu enabled = false (chưa click link email), Spring Security sẽ tự động chặn không cho đăng nhập!
                true, true, true,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}