package com.vu.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data // Thần chú của Lombok: Tự động sinh Getter, Setter, toString... khi biên dịch
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // SQL Server sẽ tự động tăng ID này (1, 2, 3...)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    // Mật khẩu sẽ được mã hóa BCrypt loằng ngoằng trước khi lưu xuống SQL Server
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    // Trạng thái tài khoản: false = chưa xác thực email, true = đã xác thực và được phép đăng nhập
    private boolean enabled = false;

    // Chuỗi mã token ngẫu nhiên gửi qua Email để học sinh bấm vào kích hoạt tài khoản
    @Column(name = "verification_token")
    private String verificationToken;

    // Chuỗi mã token ngẫu nhiên dùng khi học sinh bấm "Quên mật khẩu"
    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    // Hình thức đăng nhập để hệ thống phân biệt: "LOCAL" (mật khẩu) hoặc "GOOGLE"
    private String provider;
}