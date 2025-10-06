package org.example.profilecase5.Controller;

import org.example.profilecase5.Model.RentalHistory;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Service.RentalHistoryService;
import org.example.profilecase5.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/hosting/rental-history")
public class RentalHistoryController {
    @Autowired
    private UserService userService;
    @Autowired
    private RentalHistoryService rentalHistoryService;

    // Add pagination parameters to the method
    @GetMapping
    public String getAllRentalHistory(Authentication authentication,
            @RequestParam(defaultValue = "0") int page,  // Page number, default 0
            @RequestParam(defaultValue = "5") int size, // Page size, default 10
            Model model) {
        if (authentication == null || authentication.getName() == null) {
            model.addAttribute("error", "User not authenticated");
            return "error";
        }

        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        model.addAttribute("user", user);

        // Validate the retrieved user
        if (user == null) {
            model.addAttribute("error", "User not found");
            return "error";
        }

        // Pageable object to manage pagination
        Pageable pageable = PageRequest.of(page, size);

        // Get paginated rental history from service
        Page<RentalHistory> rentalHistoriesPage = rentalHistoryService.getAllRentalHistory(pageable);

        // Add paginated result to model
        model.addAttribute("rentalHistories", rentalHistoriesPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", rentalHistoriesPage.getTotalPages());

        return "hosting/rentalHistory/listRentalHistories";
    }

    @GetMapping("/detail/{id}")
    public String getRentalHistoryById(@PathVariable int id, Model model) {
        RentalHistory rentalHistory = rentalHistoryService.getRentalHistoryById(id);
        model.addAttribute("rentalHistory", rentalHistory);
        return "hosting/rentalHistory/detailRentalHistory";
    }
}
