package org.example.profilecase5.Controller;


import org.example.profilecase5.Model.RentalHistory;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;
    @GetMapping()
    public String getUsers(Model model) {
        List<User> user = userService.getAllUsers();
        model.addAttribute("user", user);
        return "admin/admin"; // Tên file Thymeleaf
    }

    @PostMapping("/toggleStatus/{userId}")
    public String toggleStatus(@PathVariable int userId) {
        userService.toggleUserStatus(userId);
        return "redirect:/admin";
    }
//    @GetMapping
//    public String getUsers(@RequestParam(defaultValue = "0") int page, Model model) {
//        Page<User> usersPage = userService.getUsersWithPagination(page, 10);
//        model.addAttribute("users", usersPage.getContent());
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", usersPage.getTotalPages());
//        return "users";
//    }

    @GetMapping("/detail/{userId}")
    public String userDetails(@PathVariable int userId, Model model) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + userId); // Hoặc trả về trang thông báo lỗi
        }

        // Lấy danh sách lịch sử thuê nhà
        Set<RentalHistory> rentalHistories = user.getRentalHistories();

        // Thêm thông tin vào model
        model.addAttribute("user", user);
        model.addAttribute("rentalHistories", rentalHistories);

        // Tính tổng số tiền đã chi tiêu
        double totalSpent = rentalHistories.stream().mapToDouble(RentalHistory::getTotalCost).sum();
        model.addAttribute("totalSpent", totalSpent);

        return "admin/userDetail";  // Tên file Thymeleaf
    }

}