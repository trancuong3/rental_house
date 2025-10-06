package org.example.profilecase5.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutController {

    // Đảm bảo rằng khi logout, người dùng sẽ được chuyển hướng tới /main
    @GetMapping("/perform_logout")
    public String logout() {
        return "redirect:/main";  // Điều hướng về /main sau khi đăng xuất
    }
}
