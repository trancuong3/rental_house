package org.example.profilecase5.Controller;

import org.example.profilecase5.Model.User;
import org.example.profilecase5.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public String updateAccount( @RequestParam String fullname,
                                @RequestParam String  password,
                                @RequestParam String email, @RequestParam(required = false) String phone,
                                @RequestParam(required = false) String address, Model model) {
        int userId = 1;
        User user = userService.getUserById(userId);

        if (user != null) {

            user.setFullname(fullname);
            user.setPassword(password);
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
