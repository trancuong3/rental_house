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
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public String getAccountPage(Model model) {
        int userId = 1;
        User user = userService.getUserById(userId);
        if (user != null) {
            model.addAttribute("user", user);
            return "account";
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
            return "editAccount";
        } else {
            model.addAttribute("error", "User not found");
            return "error";
        }
    }

    @PostMapping("/update")
    public String updateAccount(@RequestParam String username, @RequestParam String fullname,
                                @RequestParam String email, @RequestParam(required = false) String phone,
                                @RequestParam(required = false) String address, Model model) {
        int userId = 1;
        User user = userService.getUserById(userId);

        if (user != null) {
            user.setUsername(username);
            user.setFullname(fullname);
            user.setEmail(email);
            user.setPhone(phone);
            user.setAddress(address);
            userService.updateUser(user);
            model.addAttribute("user", user);
            return "redirect:/account";
        } else {
            model.addAttribute("error", "User not found");
            return "error";
        }
    }
}
