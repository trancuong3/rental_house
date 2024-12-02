package org.example.profilecase5.Controller;

import org.example.profilecase5.Model.House;
import org.example.profilecase5.Model.RentalHistory;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Service.RentalHistoryService;
import org.example.profilecase5.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/hosting/rental-history")
public class RentalHistoryController {
    @Autowired
    private UserService userService;

    @Autowired
    private RentalHistoryService rentalHistoryService;

    @GetMapping
    public String getAllRentalHistory(Model model, Authentication authentication) {
        String username = authentication.getName();  // Lấy tên người dùng từ Authentication
        User user = userService.getUserByUsername(username);  // Lấy người dùng từ dịch vụ

        // Kiểm tra nếu người dùng tồn tại
        if (user != null) {
            model.addAttribute("user", user);  // Truyền người dùng vào model
        } else {
            model.addAttribute("error", "User not found");
        }

        List<RentalHistory> rentalHistories = rentalHistoryService.getAllRentalHistory();
        model.addAttribute("rentalHistories", rentalHistories);
        return "hosting/rentalHistory/listRentalHistories";
    }

    @GetMapping("/detail/{id}")
    public String getRentalHistoryById(@PathVariable int id, Model model) {
        RentalHistory rentalHistory = rentalHistoryService.getRentalHistoryById(id);
        model.addAttribute("rentalHistory", rentalHistory);
        return "hosting/rentalHistory/detailRentalHistory";

    }
}
