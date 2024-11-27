package org.example.profilecase5.Controller.profile_account;

import org.example.profilecase5.Model.User;
import org.example.profilecase5.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public String getAccountPage(Model model) {
        // Lấy thông tin người dùng từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userService.getUserByUsername(username);

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
                                 RedirectAttributes redirectAttributes) {

        // Lấy thông tin người dùng từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userService.getUserByUsername(username);

            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "Người dùng không tồn tại");
                return "redirect:/account";
            }

            // Kiểm tra mật khẩu hiện tại
            if (!userService.isPasswordCorrect(currentPassword, user.getPassword())) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không đúng");
                return "redirect:/account";
            }

            // Kiểm tra mật khẩu mới
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu mới không khớp với xác nhận");
                return "redirect:/account";
            }

            // Cập nhật mật khẩu mới
            user.setPassword(userService.encodePassword(newPassword));
            userService.saveUser(user);

            redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công");
            return "redirect:/account";
        } else {
            redirectAttributes.addFlashAttribute("error", "Người dùng chưa đăng nhập");
            return "redirect:/login";
        }
    }
}
