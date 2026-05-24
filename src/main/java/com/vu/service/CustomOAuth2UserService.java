package com.vu.service;

import com.vu.entity.User;
import com.vu.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Dòng log này sẽ hiện màu đỏ trong Console nếu nó chạy
        logger.error(">>> CustomOAuth2UserService đã được kích hoạt!");

        OAuth2User oauth2User = super.loadUser(userRequest);

        String email = oauth2User.getAttribute("email");
        String fullName = oauth2User.getAttribute("name");
        String avatarUrl = oauth2User.getAttribute("picture");

        logger.error("DEBUG: Email từ Google là: {}", email);

        User user = userRepository.findByEmail(email);

        if (user == null) {
            logger.error("DEBUG: Không tìm thấy trong DB, chuẩn bị tạo mới...");
            user = new User();
            user.setEmail(email);
            user.setFullName(fullName);
            user.setAvatarUrl(avatarUrl);
            user.setProvider("GOOGLE");
            user.setEnabled(true);
            userRepository.save(user);
            logger.error("DEBUG: Đã lưu user vào DB thành công!");
        } else {
            logger.error("DEBUG: User đã tồn tại: {}", user.getFullName());
        }

        return new CustomOAuth2User(oauth2User);
    }
}