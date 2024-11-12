package org.example.profilecase5.Controller;

import org.example.profilecase5.Model.User;
import org.example.profilecase5.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("profile")
public class profileController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public String getPage(Model model) {
        int userId = 1; // Example userId
        User user = userService.getUserById(userId);
        if (user != null) {
            model.addAttribute("user", user);
            return "profile/profile"; // Return to profile page with user details
        } else {
            model.addAttribute("error", "User not found");
            return "error";
        }
    }

    @PostMapping("/saveUrl")
    public String saveUrl(@RequestParam("url") String url, Model model) {
        int userId = 1; // Example userId
        User user = userService.getUserById(userId);

        if (user != null) {
            user.setAvatar(url); // Save the URL as avatar
            userService.updateUser(user); // Update the user in the database
            model.addAttribute("user", user);
            return "profile/profile"; // Redirect to profile page with updated avatar
        } else {
            model.addAttribute("error", "User not found");
            return "error";
        }
    }
}
