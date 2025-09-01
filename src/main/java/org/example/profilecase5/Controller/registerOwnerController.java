package org.example.profilecase5.Controller;

import org.example.profilecase5.Exception.User.EmailAlreadyExistsException;
import org.example.profilecase5.Exception.User.PasswordValidationException;
import org.example.profilecase5.Exception.User.PhoneAlreadyExistsException;
import org.example.profilecase5.Exception.User.UsernameAlreadyExistsException;
import org.example.profilecase5.Model.User;

import org.example.profilecase5.Model.WaitingOwner;
import org.example.profilecase5.Service.UserService;
import org.example.profilecase5.Service.WaitingOwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/registerOwner")
public class registerOwnerController {

    @Autowired
    private UserService userService;

    @Autowired
    private WaitingOwnerService waitingOwnerService;
    // Constructor injection

    @GetMapping
    public String showOwnerRegistrationForm(Model model) {
        model.addAttribute("user", new User());

        return "Owner/register";
    }

    @PostMapping
    public String registerOwnerUser(@Validated @ModelAttribute("user") WaitingOwner waitingOwner, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "Owner/register";
        }
        try {
            waitingOwnerService.addWaitingOwner(waitingOwner);
        } catch (UsernameAlreadyExistsException e) {
            result.rejectValue("username", "error.username", "Tên người dùng đã tồn tại.");
            return "Owner/register";
        } catch (EmailAlreadyExistsException e) {
            result.rejectValue("email", "error.email", "Email đã tồn tại.");
            return "Owner/register";
        } catch (PasswordValidationException e) {
            result.rejectValue("password", "error.password", "Mật khẩu không hợp lệ.");
            return "Owner/register";
        } catch (PhoneAlreadyExistsException e) {
            result.rejectValue("phone", "error.phone", "Số điện thoại đã tồn tại.");
            return "Owner/register";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Đăng ký chủ nhà thành công! Vui lòng đăng nhập.");
        return "redirect:/login";
    }
}
