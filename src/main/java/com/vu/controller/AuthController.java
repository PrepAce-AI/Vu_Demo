package com.vu.controller;

import com.vu.entity.User;
import com.vu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // Hiển thị giao diện đăng ký
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    // Nhận dữ liệu khi người dùng bấm Submit form đăng ký
    @PostMapping("/register")
    public String processRegister(@ModelAttribute User user) {
        try {
            // Gọi tầng Service để lưu DB
            userService.registerNewUser(user);

            // Nếu thành công, chuyển hướng người dùng về trang đăng nhập kèm thông báo
            return "redirect:/login?success";
        } catch (Exception e) {
            // Nếu lỗi (ví dụ trùng email), load lại trang đăng ký kèm thông báo lỗi
            return "redirect:/register?error";
        }
    }

    // Hiển thị giao diện đăng nhập (tạm thời chưa có HTML)
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam("token") String token) {
        boolean isVerified = userService.verifyUserAccount(token);
        if (isVerified) {
            // Xác thực thành công, đẩy về trang đăng nhập
            return "redirect:/login?verified";
        } else {
            // Xác thực thất bại
            return "redirect:/login?invalid_token";
        }
    }
    // 1. Hiển thị trang nhập Email quên mật khẩu
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    // 2. Xử lý khi user bấm nút gửi yêu cầu lấy lại mật khẩu
    @PostMapping("/forgot-password")
    public String processForgotPassword(jakarta.servlet.http.HttpServletRequest request) {
        String email = request.getParameter("email");
        String token = java.util.UUID.randomUUID().toString();

        try {
            // Sinh token và lưu vào DB
            userService.updateResetPasswordToken(token, email);
            // Bắn email chứa link đổi mật khẩu
            userService.sendResetPasswordEmail(email, token);

            return "redirect:/forgot-password?success";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/forgot-password?error_not_found";
        }
    }

    // 3. Hiển thị trang nhập Mật khẩu mới khi user click từ link Email
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, org.springframework.ui.Model model) {
        User user = userService.getByResetPasswordToken(token);
        if (user == null) {
            return "redirect:/login?error=invalid_token";
        }

        // Truyền token sang bên HTML ẩn để tí nữa submit lên biết đổi cho ai
        model.addAttribute("token", token);
        return "reset-password";
    }

    // 4. Xử lý lưu mật khẩu mới xuống DB
    @PostMapping("/reset-password")
    public String processResetPassword(jakarta.servlet.http.HttpServletRequest request) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");

        User user = userService.getByResetPasswordToken(token);
        if (user == null) {
            return "redirect:/login?error=invalid_token";
        }

        // Cập nhật mật khẩu mới và xóa token
        userService.updatePassword(user, password);

        return "redirect:/login?reset_success";
    }
}