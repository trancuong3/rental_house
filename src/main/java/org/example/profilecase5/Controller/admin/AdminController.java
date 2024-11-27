package org.example.profilecase5.Controller.admin;


import org.example.profilecase5.Model.House;
import org.example.profilecase5.Model.RentalHistory;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Model.WaitingOwner;
import org.example.profilecase5.Service.HouseService;
import org.example.profilecase5.Service.UserService;
import org.example.profilecase5.Service.WaitingOwnerService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private HouseService houseService;
    @Autowired
    private WaitingOwnerService waitingOwnerService;
    @GetMapping()
    public String getUsers(Model model) {
        List<User> user = userService.getAllUsers();
        model.addAttribute("user", user);
        return "admin/admin"; // Tên file Thymeleaf
    }

    @PostMapping("/toggleStatus/{userId}")
    public String toggleStatus(@PathVariable int userId) {
        userService.toggleUserStatus(userId);  // Toggle user status
        return "redirect:/admin";  // Redirect to admin page
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
    @GetMapping("/house")
    public String house(Model model) {
        // Lấy danh sách tất cả nhà
        List<House> house = houseService.getAllHouses();

        // Lấy top 5 căn nhà có nhiều lượt thuê nhất
        List<House> topHouses = houseService.getTop5MostRentedHouses();

        // Thêm danh sách nhà và top 5 vào model
        model.addAttribute("house", house);          // Danh sách tất cả nhà
        model.addAttribute("topHouses", topHouses);  // Top 5 căn nhà có nhiều lượt thuê nhất

        return "admin/house";
    }


    @GetMapping("/waiting-owners")
    public String showWaitingOwners(Model model) {
        List<WaitingOwner> waitingOwners = waitingOwnerService.getAllWaitingOwners();
        model.addAttribute("waitingOwners", waitingOwners);
        return "admin/waiting-owners"; // Trang JSP cho danh sách chờ duyệt
    }

    // Accept waiting owner
    @PostMapping("/waiting-owners/accept/{id}")
    public String acceptOwner(@PathVariable("id") int id) {
        // Gọi service để chấp nhận chủ nhà và chuyển sang bảng user
        waitingOwnerService.acceptWaitingOwner(id);

        // Sau khi chuyển thành công, chuyển hướng lại trang danh sách chủ nhà chờ duyệt
        return "redirect:/admin/waiting-owners";
    }
    // Refuse waiting owner
    @PostMapping("/waiting-owners/refuse/{id}")
    public String refuseOwner(@PathVariable("id") int id) {
        waitingOwnerService.refuseWaitingOwner(id);
        return "redirect:/admin/waiting-owners";
    }

    @GetMapping("/owners")
    public String listOwners(Model model) {
        List<User> owners = userService.getAllOwners();
        model.addAttribute("owners", owners); // Đảm bảo biến "owners" được truyền
        return "admin/owner-list";
    }

}