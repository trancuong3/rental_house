package org.example.profilecase5.Controller;

import java.sql.Timestamp;
import java.util.List;

import org.example.profilecase5.Model.PasswordHistory;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Repository.PasswordHistoryRepository;
import org.example.profilecase5.Repository.UserRepository;
import org.example.profilecase5.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("")
    public String getAccountPage(Model model) {
        int userId = 1;
        User user = userService.getUserById(userId);
        if (user != null) {
            model.addAttribute("user", user);
            return "account/account";
        } else {
            model.addAttribute("error", "User not found");
            return "error";
        }
    }

    @GetMapping("/edit")
    public String getEditAccountPage(Model model) {
        int userId = 1;
        User user = userService.getUserById(userId);
        if (user != null) {
            model.addAttribute("user", user);
            return "account/editAccount";
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
            return "redirect:/account";
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
            return "redirect:/account";
        }

        if (!currentPassword.equals(user.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không đúng");
            return "redirect:/account";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới không khớp với xác nhận");
            return "redirect:/account";
        }

        user.setPassword(newPassword);
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công");
        return "redirect:/account";
    }


}
