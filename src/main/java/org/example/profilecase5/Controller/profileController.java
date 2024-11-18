package org.example.profilecase5.Controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.List;

import net.coobird.thumbnailator.Thumbnails;
import org.example.profilecase5.Model.PasswordHistory;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Repository.PasswordHistoryRepository;
import org.example.profilecase5.Repository.UserRepository;
import org.example.profilecase5.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



@Controller
@RequestMapping("/profile")
public class profileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordHistoryRepository passwordHistoryRepository;

    @Autowired
    private UserService userService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @GetMapping("")
    public String getAccountPage(Model model) {
        int userId = 1;
        User user = userService.getUserById(userId);
        if (user != null) {
            model.addAttribute("user", user);
            return "profile/profile";
        } else {
            model.addAttribute("error", "User not found");
            return "error";
        }
    }


    @PostMapping("/update")
    public String updateAccount(@RequestParam String fullname,
                                @RequestParam String email,
                                @RequestParam(required = false) String phone,
                                @RequestParam(required = false) String address,
                                Model model) {
        int userId = 1;
        User user = userService.getUserById(userId);

        if (user != null) {
            user.setFullname(fullname);
            user.setEmail(email);
            user.setPhone(phone);
            user.setAddress(address);

            userService.updateUser(user);
            return "redirect:/profile";
        } else {
            model.addAttribute("error", "User not found");
            return "error";
        }
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes, Model model) {

        int userId = 1;
        User user = userService.getUserById(userId);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Người dùng không tồn tại");
            return "redirect:/profile";
        }

        // Kiểm tra mật khẩu hiện tại đã mã hóa
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không đúng");
            return "redirect:/profile";
        }

        // Kiểm tra khớp của mật khẩu mới và xác nhận mật khẩu
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới không khớp với xác nhận");
            return "redirect:/profile";
        }

        // Mã hóa mật khẩu mới trước khi lưu
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công");
        return "redirect:/profile";
    }
    @PostMapping("/update-avatar")
    public String updateAvatar(@RequestParam("avatar") MultipartFile file, Model model) {
        try {
            if (file.getSize() > MAX_FILE_SIZE) {
                model.addAttribute("message", "Ảnh quá lớn, vui lòng chọn ảnh nhỏ hơn 5MB.");
                return "profile/profile";
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(file.getInputStream())
                    .size(200, 200)
                    .outputFormat("JPEG")
                    .outputQuality(0.8f)
                    .toOutputStream(outputStream);

            byte[] resizedImage = outputStream.toByteArray();
            String base64Avatar = Base64.getEncoder().encodeToString(resizedImage);

            User user = userService.getUserById(1);
            user.setAvatar(base64Avatar);
            userService.saveUser(user);
        } catch (IOException e) {

            model.addAttribute("message", "Đã xảy ra lỗi khi cập nhật ảnh đại diện. Vui lòng thử lại.");
        }
        return "redirect:/profile";
    }


}
