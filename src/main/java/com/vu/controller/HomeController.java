package com.vu.controller;

import com.vu.entity.User;
import com.vu.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

    // Đường dẫn mặc định khi đăng nhập thành công
    @GetMapping({"/", "/profile"})
    public String showProfile(Authentication authentication, Model model) {
        if (authentication == null) return "redirect:/login";

        String email = "";

        // KIỂM TRA: Nếu người dùng đăng nhập bằng Google
        if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.user.OAuth2User) {
            org.springframework.security.oauth2.core.user.OAuth2User oauth2User =
                    (org.springframework.security.oauth2.core.user.OAuth2User) authentication.getPrincipal();
            // Ép nó lấy chính xác chữ "email"
            email = oauth2User.getAttribute("email");
        } else {
            // Nếu đăng nhập bằng tài khoản đăng ký tay
            email = authentication.getName();
        }

        // Mang email thật đi tìm trong Database
        User user = userRepository.findByEmail(email);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setFullName("Người dùng mới");
        }

        model.addAttribute("user", user);
        return "profile";
    }
    @GetMapping("/edit-profile")
    public String showEditProfileForm(Authentication authentication, Model model) {
        String email = "";

        // Trích xuất email y hệt như hàm showProfile
        if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.user.OAuth2User) {
            org.springframework.security.oauth2.core.user.OAuth2User oauth2User =
                    (org.springframework.security.oauth2.core.user.OAuth2User) authentication.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else {
            email = authentication.getName();
        }

        User user = userRepository.findByEmail(email);

        // Nếu user chưa kịp lưu, đẩy về trang profile thay vì báo lỗi 500
        if (user == null) {
            return "redirect:/profile";
        }

        model.addAttribute("user", user);
        return "edit-profile";
    }

    // 2. Hàm xử lý dữ liệu Submit lên
    @PostMapping("/edit-profile")
    public String updateProfile(
            Authentication authentication,
            @RequestParam("fullName") String fullName,
            @RequestParam("avatarFile") MultipartFile file) {

        // Lấy thông tin user hiện tại
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);

        // Cập nhật tên mới
        user.setFullName(fullName);

        // XỬ LÝ UPLOAD ẢNH (Nếu người dùng có chọn ảnh mới)
        if (!file.isEmpty()) {
            try {
                // Tạo thư mục "uploads" ở gốc dự án nếu chưa có
                Path uploadPath = Paths.get("uploads");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Đổi tên file ảnh (thêm mã UUID ngẫu nhiên để không bị trùng tên file)
                String originalFilename = file.getOriginalFilename();
                String newFileName = UUID.randomUUID().toString() + "_" + originalFilename;

                // Copy ảnh từ form vào ổ cứng máy tính
                Path filePath = uploadPath.resolve(newFileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Cập nhật đường dẫn ảnh vào Database
                user.setAvatarUrl("/uploads/" + newFileName);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Lưu toàn bộ thông tin mới xuống SQL Server
        userRepository.save(user);

        // Đổi xong thì đẩy về lại trang form kèm thông báo thành công
        return "redirect:/edit-profile?updated";
    }
}