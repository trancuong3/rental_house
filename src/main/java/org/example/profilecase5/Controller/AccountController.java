package org.example.profilecase5.Controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import net.coobird.thumbnailator.Thumbnails;
import org.example.profilecase5.Model.PasswordHistory;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Repository.PasswordHistoryRepository;
import org.example.profilecase5.Repository.UserRepository;
import org.example.profilecase5.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordHistoryRepository passwordHistoryRepository;

    @Autowired
    private UserService userService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @GetMapping("")
    public String getAccountPage(Model model, HttpSession session) {
        // Lấy userId từ session
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId != null) {
            User user = userService.getUserById(userId);
            if (user != null) {
                model.addAttribute("user", user);
                return "account/account";
            } else {
                model.addAttribute("error", "User not found");
                return "error";
            }
        } else {
            model.addAttribute("error", "User not logged in");
            return "error";
        }
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes redirectAttributes,
                                 HttpSession session) {

        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            redirectAttributes.addFlashAttribute("error", "Người dùng chưa đăng nhập");
            return "redirect:/login";
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Người dùng không tồn tại");
            return "redirect:/account";
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không đúng");
            return "redirect:/account";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới không khớp với xác nhận");
            return "redirect:/account";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công");
        return "redirect:/account";
    }
}