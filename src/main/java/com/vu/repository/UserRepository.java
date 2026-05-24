package com.vu.repository;

import com.vu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Tự động sinh ra câu lệnh SQL: SELECT * FROM users WHERE email = ?
    User findByEmail(String email);

    // Tìm tài khoản dựa vào mã token gửi qua email
    User findByVerificationToken(String verificationToken);
    User findByResetPasswordToken(String token);
}