package org.example.profilecase5.Controller;

import org.example.profilecase5.Model.User;
import org.example.profilecase5.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("profile")
public class profileController {
    @Autowired
    private UserService userService;

    @GetMapping("")
    public String getPage(Model model) {
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
}
