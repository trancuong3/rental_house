package org.example.profilecase5.Controller;

import org.example.profilecase5.Model.User;
import org.example.profilecase5.Service.UserService;
import org.example.profilecase5.Service.RentalHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private RentalHistoryService rentalHistoryService;

    // ---------------- DANH SÁCH USER ----------------
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/user-list"; // Trang hiển thị danh sách user
    }

    // ---------------- CHI TIẾT USER ----------------
    @GetMapping("/user/{id}")
    public String viewUserDetail(@PathVariable("id") int id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            model.addAttribute("errorMessage", "Không tìm thấy người dùng!");
            return "redirect:/admin";
        }
        model.addAttribute("user", user);
        model.addAttribute("rentalHistories", rentalHistoryService.getRentalHistoryByUserId(id));

        // Tính tổng tiền đã chi tiêu
        double totalSpent = rentalHistoryService.calculateTotalSpentByUser(id);
        model.addAttribute("totalSpent", totalSpent);

        return "admin/user-detail";
    }

    // ---------------- XÓA USER ----------------
    @PostMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") int id,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUserById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa người dùng này!");
        }
        return "redirect:/admin";
    }

    // ---------------- XÓA OWNER ----------------
    @PostMapping("/owner/delete/{id}")
    public String deleteOwner(@PathVariable("id") int id,
                              RedirectAttributes redirectAttributes) {
        try {
            User owner = userService.getUserById(id);
            if (owner == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy Owner để xóa!");
                return "redirect:/admin/owners";
            }

            // Chỉ xóa nếu là chủ nhà
            if (owner.getRole() != null && owner.getRole().getRoleId() == 3) {
                userService.deleteUserById(id);
                redirectAttributes.addFlashAttribute("successMessage", "Xóa Owner thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Người này không phải là Owner!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa Owner: " + e.getMessage());
        }

        return "redirect:/admin/owners";
    }

    // ---------------- DANH SÁCH OWNER ----------------
    @GetMapping("/owners")
    public String listOwners(Model model) {
        model.addAttribute("owners", userService.getAllOwners());
        return "admin/owner-list"; // Trang danh sách chủ nhà
    }

    // ---------------- DUYỆT OWNER CHỜ ----------------
    @GetMapping("/waiting-owners")
    public String listWaitingOwners(Model model) {
        model.addAttribute("waitingOwners", userService.getWaitingOwners());
        return "admin/waiting-owners";
    }

    // ---------------- DUYỆT CHỦ NHÀ ----------------
    @PostMapping("/approve-owner/{id}")
    public String approveOwner(@PathVariable("id") int id,
                               RedirectAttributes redirectAttributes) {
        try {
            userService.approveOwner(id);
            redirectAttributes.addFlashAttribute("successMessage", "Duyệt chủ nhà thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể duyệt chủ nhà này!");
        }
        return "redirect:/admin/waiting-owners";
    }

}
