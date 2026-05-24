package com.vu.service;

import com.vu.entity.User;
import com.vu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    // ==================== CỤM TÍNH NĂNG ĐĂNG KÝ & XÁC THỰC ====================

    public void registerNewUser(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Email này đã được sử dụng!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setProvider("LOCAL");
        user.setEnabled(false);

        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);

        userRepository.save(user);
        sendVerificationEmail(user.getEmail(), token);
    }

    public boolean verifyUserAccount(String token) {
        User user = userRepository.findByVerificationToken(token);
        if (user == null || user.isEnabled()) {
            return false;
        }

        user.setEnabled(true);
        user.setVerificationToken(null);
        userRepository.save(user);
        return true;
    }

    private void sendVerificationEmail(String recipientEmail, String token) {
        String url = "http://localhost:8080/verify?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject("Xác nhận đăng ký tài khoản Học Tập");
        message.setText("Chào bạn,\n\nVui lòng click vào đường link dưới đây để kích hoạt tài khoản của bạn:\n" + url);

        mailSender.send(message);
    }

    // ==================== CỤM TÍNH NĂNG QUÊN MẬT KHẨU (MỚI) ====================

    // 1. Tạo mã Token quên mật khẩu và lưu vào DB khi user yêu cầu
    public void updateResetPasswordToken(String token, String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setResetPasswordToken(token);
            userRepository.save(user);
        } else {
            throw new RuntimeException("Không tìm thấy tài khoản nào ứng với email: " + email);
        }
    }

    // 2. Tìm User dựa vào mã token trên đường link email gửi về
    public User getByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token);
    }

    // 3. Tiến hành cập nhật mật khẩu mới và xóa token đi để không dùng lại được nữa
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null); // Xóa token bảo mật sau khi đổi xong
        userRepository.save(user);
    }

    // 4. Hàm bắn mail chứa link đặt lại mật khẩu cho User
    public void sendResetPasswordEmail(String recipientEmail, String token) {
        String url = "http://localhost:8080/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject("Yêu cầu đặt lại mật khẩu - Hệ thống Học Tập");
        message.setText("Chào bạn,\n\nBạn đã gửi yêu cầu đặt lại mật khẩu.\nVui lòng click vào đường link dưới đây để thiết lập mật khẩu mới:\n" + url + "\n\nNếu bạn không yêu cầu điều này, hãy bỏ qua email này.");

        mailSender.send(message);
    }
}