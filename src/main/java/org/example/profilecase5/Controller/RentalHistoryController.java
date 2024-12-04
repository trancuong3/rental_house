package org.example.profilecase5.Controller;

import org.example.profilecase5.Model.House;
import org.example.profilecase5.Model.RentalHistory;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Service.HouseService;
import org.example.profilecase5.Service.RentalHistoryService;
import org.example.profilecase5.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/hosting/rental-history")
public class RentalHistoryController {
    @Autowired
    private UserService userService;

    @Autowired
    private HouseService houseService;

    @Autowired
    private RentalHistoryService rentalHistoryService;

    @GetMapping
    public String getAllRentalHistory(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);

        if (user != null) {
            model.addAttribute("user", user);
            List<House> houses = houseService.getHousesByUserId(user.getUserId());
            List<RentalHistory> rentalHistories = rentalHistoryService.getRentalHistoriesByHouses(houses);
            model.addAttribute("rentalHistories", rentalHistories);
        } else {
            model.addAttribute("error", "User not found");
            return "redirect:/login";
        }

        return "hosting/rentalHistory/listRentalHistories";
    }

    @GetMapping("/detail/{id}")
    public String getRentalHistoryById(@PathVariable int id, Model model) {
        RentalHistory rentalHistory = rentalHistoryService.getRentalHistoryById(id);
        model.addAttribute("rentalHistory", rentalHistory);
        return "hosting/rentalHistory/detailRentalHistory";
    }

    @GetMapping("/check-in/{id}")
    public String checkIn(@PathVariable int id) {
        RentalHistory rentalHistory = rentalHistoryService.getRentalHistoryById(id);
        rentalHistoryService.checkIn(rentalHistory);
        return "redirect:/hosting/rental-history";
    }

    @GetMapping("/check-out/{id}")
    public String checkOut(@PathVariable int id) {
        RentalHistory rentalHistory = rentalHistoryService.getRentalHistoryById(id);
        rentalHistoryService.checkOut(rentalHistory);
        return "redirect:/hosting/rental-history";
    }

    @GetMapping("/cancel/{id}")
    public String cancel(@PathVariable int id) {
        RentalHistory rentalHistory = rentalHistoryService.getRentalHistoryById(id);
        rentalHistoryService.cancel(rentalHistory);
        return "redirect:/hosting/rental-history";
    }

    @GetMapping("/api/search")
    public ResponseEntity<Page<RentalHistory>> searchRentalHistories(
            @RequestParam(required = false) String propertyName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) RentalHistory.RentalStatus status,
            @PageableDefault(size = 10, sort = "startDate") Pageable pageable,
            Model model, Authentication authentication) {
        Timestamp startTimestamp = (startDate != null) ? Timestamp.valueOf(startDate) : null;
        Timestamp endTimestamp = (endDate != null) ? Timestamp.valueOf(endDate) : null;
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        if (user != null) {
            model.addAttribute("user", user);
        } else {
            model.addAttribute("error", "User not found");
            return ResponseEntity.badRequest().build();
        }
        Page<RentalHistory> rentalHistories = rentalHistoryService.searchRentalHistories(propertyName, startTimestamp, endTimestamp, status, user, pageable);
        return ResponseEntity.ok(rentalHistories);
    }
}
