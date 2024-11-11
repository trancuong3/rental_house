package org.example.profilecase5.Controller;


import org.example.profilecase5.Model.User;
import org.example.profilecase5.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;

    @GetMapping("")
    public String admin(Model model) {
        List<User> users = userService.getAllUsers();

        // Add the list of users to the model
        model.addAttribute("users", users);

        // Return the view name (the Thymeleaf template)
        return "admin/admin"; // This should match the template file name
    }
}