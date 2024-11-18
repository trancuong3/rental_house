package org.example.profilecase5.Controller;

import org.example.profilecase5.Exception.User.EmailAlreadyExistsException;
import org.example.profilecase5.Exception.User.PasswordValidationException;
import org.example.profilecase5.Exception.User.UsernameAlreadyExistsException;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


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
        return "register";
    }

    @PostMapping
    public String registerUser(@Validated @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            System.out.println(result.getAllErrors());
            return "register";
        }
        try {
            userService.registerUser(user);
        } catch (UsernameAlreadyExistsException e) {
            result.rejectValue("username", "error.username", e.getMessage());
            return "register";
        } catch (EmailAlreadyExistsException e) {
            result.rejectValue("email", "error.email", e.getMessage());
            return "register";
        } catch (PasswordValidationException e) {
            result.rejectValue("password", "error.password", e.getMessage());
            return "register";

        }
        return "redirect:/login";
    }


    @GetMapping("/owner")
    public String showOwnerRegistrationForm() {
        return "owner/register";
    }
    @PostMapping("/owner")
    public String registerOwnerUser(@RequestParam String password, Model model) {
        User user = userService.getCurrentUser();
        userService.registerOwnerUser(user);
        return "redirect:/login/owner";
    }

}