package org.example.profilecase5.Controller;

import org.example.profilecase5.Model.House;
import org.example.profilecase5.Model.RentalHistory;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Repository.HouseRepository;
import org.example.profilecase5.Service.HouseService;
import org.example.profilecase5.Service.RentalHistoryService;
import org.example.profilecase5.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
@Controller
@RequestMapping("rental")
public class RentalController {

    @Autowired
    private HouseService houseService;

    @Autowired
    private RentalHistoryService rentalHistoryService;

    @Autowired
    private UserService userService;

    @GetMapping("/confirm")
    public String submitRental(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam("houseId") int houseId,
            Model model
    ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User currentUser = userService.getUserByUsername(username);

            if (currentUser == null) {
                model.addAttribute("error", "Người dùng không tồn tại");
                return "error";
            }

            if (startDate.isAfter(endDate)) {
                model.addAttribute("error", "Ngày bắt đầu trước ngày kết thúc rồi. Nhập lại ik ba ơii");
                return "redirect:/house/detail/" + houseId;
            }

            Optional<House> optionalHouse = houseService.findById(houseId);
            if (optionalHouse.isEmpty()) {
                model.addAttribute("error", "Không tìm thấy căn nhà với ID: " + houseId);
                return "redirect:/home";
            }

            House house = optionalHouse.get();

            long numDays = ChronoUnit.DAYS.between(startDate, endDate);
            double totalCost = numDays * house.getPricePerDay().doubleValue();

            model.addAttribute("house", house);
            model.addAttribute("startDate", Timestamp.valueOf(startDate));
            model.addAttribute("endDate", Timestamp.valueOf(endDate));
            model.addAttribute("numDays", numDays);
            model.addAttribute("totalCost", totalCost);

            return "rental/rental-confirmation";
        } else {
            model.addAttribute("error", "Người dùng chưa đăng nhập");
            return "redirect:/login";
        }
    }
    @PostMapping("/confirm")
    public String confirmRental(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.S") LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.S") LocalDateTime endDate,
            @RequestParam("houseId") int houseId,
            Model model
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User currentUser = userService.getUserByUsername(username);

            if (currentUser == null) {
                model.addAttribute("error", "Người dùng không tồn tại");
                return "error";
            }
            Optional<House> optionalHouse = houseService.findById(houseId);
            if (optionalHouse.isEmpty()) {
                model.addAttribute("error", "Không tìm thấy căn nhà với ID: " + houseId);
                return "redirect:/home"; // Điều hướng về trang chủ hoặc trang danh sách nhà
            }

            House house = optionalHouse.get();

            long numDays = ChronoUnit.DAYS.between(startDate, endDate);
            double totalCost = numDays * house.getPricePerDay().doubleValue();

            model.addAttribute("house", house);
            model.addAttribute("startDate", Timestamp.valueOf(startDate));
            model.addAttribute("endDate", Timestamp.valueOf(endDate));
            model.addAttribute("numDays", numDays);
            model.addAttribute("totalCost", totalCost);

            // Lưu thông tin thuê vào cơ sở dữ liệu
            RentalHistory rental = new RentalHistory();
            rental.setStartDate(Timestamp.valueOf(startDate));
            rental.setEndDate(Timestamp.valueOf(endDate));
            rental.setTotalCost(totalCost);
            rental.setHouse(house);
            rental.setUser(currentUser); // Lưu thông tin người dùng vào rental history
            rentalHistoryService.save(rental);

            model.addAttribute("successMessage", "Thuê căn nhà thành công!");

            return "rental/rental-success"; // Chuyển đến trang thành công hoặc trang khác
        } else {
            model.addAttribute("error", "Người dùng chưa đăng nhập");
            return "redirect:/login";
        }
    }
}