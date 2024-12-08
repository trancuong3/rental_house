package org.example.profilecase5.Controller;

import org.example.profilecase5.Exception.User.EmailAlreadyExistsException;
import org.example.profilecase5.Exception.User.PasswordValidationException;
import org.example.profilecase5.Exception.User.PhoneAlreadyExistsException;
import org.example.profilecase5.Exception.User.UsernameAlreadyExistsException;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register/register";
    }

    @PostMapping
    public String registerUser(@Validated @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register/register";
        }
        try {
            userService.registerUser(user); // Hàm này sẽ kiểm tra và xử lý logic lưu user
        } catch (UsernameAlreadyExistsException e) {
            result.rejectValue("username", "error.username", "Tên người dùng đã tồn tại.");
            return "register/register";
        } catch (EmailAlreadyExistsException e) {
            result.rejectValue("email", "error.email", "Email đã tồn tại.");
            return "register/register";
        } catch (PasswordValidationException e) {
            result.rejectValue("password", "error.password", "Mật khẩu không hợp lệ.");
            return "register/register";
        } catch (PhoneAlreadyExistsException e) {
            result.rejectValue("phone", "error.phone", "Số điện thoại đã tồn tại.");
            return "register/register";
        }
        return "redirect:/login";
    }




}
