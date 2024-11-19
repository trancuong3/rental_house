package org.example.profilecase5.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
                               RedirectAttributes redirectAttributes,
                               HttpServletRequest request) {
        try {
            // Kiểm tra thông tin đăng nhập và role
            boolean isValid = userService.validateUserAndRole(username, password, selectedRole);

            if (isValid) {
                // Lấy đối tượng Authentication để xác thực người dùng
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, password);

                // Cập nhật Authentication trong SecurityContext
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // Lấy thông tin người dùng từ Authentication
                User user = userService.getUserByUsername(username);  // Tìm người dùng từ cơ sở dữ liệu
                HttpSession session = request.getSession();

                // Lưu id người dùng vào session
                session.setAttribute("userId", user.getUserId());

                // Điều hướng dựa trên vai trò người dùng
                if ("user".equals(selectedRole)) {
                    return "redirect:/home";
                }
                if ("owner".equals(selectedRole)) {
                    return "redirect:/hosting";
                }
                if ("admin".equals(selectedRole)) {
                    return "redirect:/admin";
                }

                // Nếu vai trò không hợp lệ, quay lại trang đăng nhập
                else {
                    return "redirect:/login";
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
