package org.example.profilecase5.Controller;

import org.example.profilecase5.Model.House;
import org.example.profilecase5.Model.RentalHistory;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Service.HouseService;
import org.example.profilecase5.Service.RentalHistoryService;
import org.example.profilecase5.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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
    public String getAllRentalHistory(Authentication authentication,Model model) {
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        if (user != null) {
            model.addAttribute("user", user);
            return "hosting/rentalHistory/listRentalHistories";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/api/data")
    @ResponseBody
    public ResponseEntity<?> getAllRentalHistoryData(Authentication authentication, 
                                                     @RequestParam(name = "page", defaultValue = "0") int page,
                                                     @RequestParam(name = "size", defaultValue = "10") int size) {
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);

        if (user != null) {
            List<House> houses = houseService.getHousesByUserId(user.getUserId());
            Pageable pageable = PageRequest.of(page, size);
            Page<RentalHistory> rentalHistoriesPage = rentalHistoryService.getRentalHistoriesByHouses(houses, pageable);
            return ResponseEntity.ok(rentalHistoriesPage);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
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
    @ResponseBody
    public ResponseEntity<?> searchRentalHistories(Authentication authentication,
                                                   @RequestParam(name = "page", defaultValue = "0") int page,
                                                   @RequestParam(name = "size", defaultValue = "5") int size,
                                                   @RequestParam(required = false) String propertyName,
                                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                   @RequestParam(required = false) RentalHistory.RentalStatus status){
        Timestamp startTimestamp = (startDate != null) ? Timestamp.valueOf(startDate) : null;
        Timestamp endTimestamp = (endDate != null) ? Timestamp.valueOf(endDate) : null;
        System.out.println("startTimestamp: " + startTimestamp);
        System.out.println("endTimestamp: " + endTimestamp);
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        if (user != null) {
            List<House> houses = houseService.getHousesByUserId(user.getUserId());
            Pageable pageable = PageRequest.of(page, size);
            Page<RentalHistory> rentalHistoriesPage = rentalHistoryService.searchRentalHistories(houses, propertyName, startTimestamp, endTimestamp, status, pageable);
            return ResponseEntity.ok(rentalHistoriesPage);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
}
