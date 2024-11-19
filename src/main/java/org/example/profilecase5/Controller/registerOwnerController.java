package org.example.profilecase5.Controller;

import org.example.profilecase5.Exception.User.EmailAlreadyExistsException;
import org.example.profilecase5.Exception.User.PasswordValidationException;
import org.example.profilecase5.Exception.User.UsernameAlreadyExistsException;
import org.example.profilecase5.Model.User;

import org.example.profilecase5.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/registerOwner")
public class registerOwnerController {

    @Autowired
    private UserService userService;

    // Constructor injection

    @GetMapping
    public String showOwnerRegistrationForm(Model model) {
        model.addAttribute("user", new User());

        return "Owner/register";
    }
    @PostMapping("")
    public String registerOwnerUser(@Validated @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "Owner/register";
        }
        try {
            userService.registerOwnerUser(user);
        } catch (UsernameAlreadyExistsException e) {
            result.rejectValue("username", "error.username", e.getMessage());
            return "Owner/register";
        } catch (EmailAlreadyExistsException e) {
            result.rejectValue("email", "error.email", e.getMessage());
            return "Owner/register";
        } catch (PasswordValidationException e) {
            result.rejectValue("password", "error.password", e.getMessage());
            return "Owner/register";
        }
        return "redirect:/login";
    }
}
