package org.example.profilecase5.Controller;

import jakarta.servlet.http.HttpSession;
import org.example.profilecase5.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String login(Model model) {
        return "login/login";
    }
    @GetMapping("/hosting")
    public String userDashboard(Authentication authentication) {
        return "hosting/hosting";
    }

    @PostMapping("/perform_login")
    public String processLogin(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String selectedRole,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra thông tin đăng nhập và role
            boolean isValid = userService.validateUserAndRole(username, password, selectedRole);

            if (isValid) {
                if ("user".equals(selectedRole)) {
                    return "redirect:/hosting";
                } else {
                    return "redirect:/admin";
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Invalid credentials or role");
                return "redirect:/login";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred during login");
            return "redirect:/login";
        }
    }
}