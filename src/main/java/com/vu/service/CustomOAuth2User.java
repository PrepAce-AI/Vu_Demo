package com.vu.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private OAuth2User oauth2User;

    public CustomOAuth2User(OAuth2User oauth2User) {
        this.oauth2User = oauth2User;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oauth2User.getAuthorities();
    }

    // GHI ĐÈ HÀM NÀY: Bắt buộc trả về Email của Google để đồng bộ với HomeController
    @Override
    public String getName() {
        return oauth2User.getAttribute("email");
    }

    // Viết thêm hàm tiện ích lấy tên thật và avatar
    public String getFullName() {
        return oauth2User.getAttribute("name");
    }

    public String getAvatarUrl() {
        return oauth2User.getAttribute("picture");
    }
}