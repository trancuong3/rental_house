package org.example.profilecase5.Controller;

import org.apache.tomcat.util.codec.binary.Base64;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Files;

@Controller
@RequestMapping("/hosting")
public class HostingController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public String getAccountPage(Model model, Authentication authentication) {
        String username = authentication.getName();  // Lấy username của người dùng hiện tại
        User user = userService.getUserByUsername(username);  // Tìm người dùng từ username

        if (user == null) {
            model.addAttribute("error", "User not found");
            return "error";  // Nếu không tìm thấy người dùng
        }

        // Nếu không có avatar, không cần chuyển đổi base64 cho ảnh mặc định
        if (user.getAvatar() == null || user.getAvatar().isEmpty()) {
            user.setAvatar(null);  // Không gán giá trị base64
        }

        model.addAttribute("user", user);
        return "hosting/hosting";  // Trả về trang profile của người dùng
    }
}
