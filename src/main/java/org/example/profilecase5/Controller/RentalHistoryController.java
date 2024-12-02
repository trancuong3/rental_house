package org.example.profilecase5.Controller;

import org.example.profilecase5.Model.RentalHistory;
import org.example.profilecase5.Service.RentalHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/hosting/rental-history")
public class RentalHistoryController {

    @Autowired
    private RentalHistoryService rentalHistoryService;

    @GetMapping
    public String getAllRentalHistory(Model model) {
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
