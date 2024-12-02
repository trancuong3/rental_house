package org.example.profilecase5.Controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import org.springframework.security.core.Authentication;
import net.coobird.thumbnailator.Thumbnails;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Repository.UserRepository;
import org.example.profilecase5.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/profile")
public class profileController {

    @Autowired
    private UserRepository userRepository;



    @Autowired
    private UserService userService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @GetMapping("")
    public String getAccountPage(Model model, Authentication authentication) {
        String username = authentication.getName();  // Lấy username của người dùng hiện tại
        User user = userService.getUserByUsername(username);  // Tìm người dùng từ username

        if (user != null) {
            model.addAttribute("user", user);
            return "profile/profile";  // Trả về trang profile của người dùng
        } else {
            model.addAttribute("error", "User not found");
            return "error";  // Nếu không tìm thấy người dùng
        }
    }



    @PostMapping("/update")
    public String updateAccount(@RequestParam String fullname,
                                @RequestParam String email,
                                @RequestParam(required = false) String phone,
                                @RequestParam(required = false) String address,
                                Authentication authentication,
                                Model model) {

        String username = authentication.getName();  // Lấy username của người dùng hiện tại
        User user = userService.getUserByUsername(username);  // Tìm người dùng từ username

        if (user != null) {
            user.setFullname(fullname);
            user.setEmail(email);
            user.setPhone(phone);
            user.setAddress(address);

            userService.updateUser(user);  // Cập nhật người dùng
            return "redirect:/profile";  // Quay lại trang profile
        } else {
            model.addAttribute("error", "User not found");
            return "error";  // Nếu không tìm thấy người dùng
        }
    }



    @PostMapping("/update-avatar")
    public String updateAvatar(@RequestParam("avatar") MultipartFile file, Authentication authentication, Model model) {
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

            String username = authentication.getName();  // Lấy username của người dùng hiện tại
            User user = userService.getUserByUsername(username);  // Tìm người dùng từ username
            if (user != null) {
                user.setAvatar(base64Avatar);
                userService.saveUser(user);  // Cập nhật avatar
            }
        } catch (IOException e) {
            model.addAttribute("message", "Đã xảy ra lỗi khi cập nhật ảnh đại diện. Vui lòng thử lại.");
        }
        return "redirect:/profile";
    }



}
