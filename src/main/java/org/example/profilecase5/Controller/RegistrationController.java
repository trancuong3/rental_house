package org.example.profilecase5.Controller;


import org.example.profilecase5.Exception.User.UsernameAlreadyExistsException;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;

@Controller
@RequestMapping("/register")
public class RegistrationController {
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register/register";
    }

    @PostMapping
    public String registerUser(@ModelAttribute("user") User user, BindingResult result, Model model) {
//        if (result.hasErrors()) {
//            System.out.println(1);
//            return "register";
//        }

        // Thiết lập thời gian hiện tại cho createdAt và updatedAt
        Timestamp currentTimestamp = Timestamp.from(Instant.now());
        user.setCreatedAt(currentTimestamp);
        user.setUpdatedAt(currentTimestamp);

        try {
            userService.registerUser(user);
            return "redirect:/login";
        } catch (UsernameAlreadyExistsException e) {
            model.addAttribute("usernameError", e.getMessage());
            return "register/register";
        }
    }
}