package org.example.profilecase5.Controller;


import org.example.profilecase5.Model.House;
import org.example.profilecase5.Model.RentalHistory;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Model.WaitingOwner;
import org.example.profilecase5.Service.EmailService;
import org.example.profilecase5.Service.HouseService;
import org.example.profilecase5.Service.UserService;
import org.example.profilecase5.Service.WaitingOwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    @Autowired
    private EmailService emailService;
    @GetMapping("")
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
    @GetMapping("/house")
    public String house(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        // Create a Pageable object with the page and size parameters
        Pageable pageable = PageRequest.of(page, size);

        // Get paginated list of houses
        Page<House> housePage = houseService.getHouses(pageable);

        // Get top 5 rented houses
        List<House> topHouses = houseService.getTop5MostRentedHouses();

        // Add the houses and pagination info to the model
        model.addAttribute("housePage", housePage);
        model.addAttribute("topHouses", topHouses);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", housePage.getTotalPages());

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
        // Gọi service để chấp nhận chủ nhà và lấy email của họ
        WaitingOwner waitingOwner = waitingOwnerService.findById(id);
        if (waitingOwner == null) {
            throw new RuntimeException("WaitingOwner not found with id: " + id);
        }
        String email = waitingOwner.getEmail();

        // Chuyển đổi và lưu trữ vào bảng user
        waitingOwnerService.acceptWaitingOwner(id);

        // Gửi email thông báo
        emailService.sendEmail(
                email,
                "Đăng ký làm chủ nhà được chấp nhận",
                "Đăng ký làm chủ nhà được chấp nhận\n" +

                        "                        Chúng tôi xin trân trọng thông báo rằng đơn đăng ký của quý vị vào  đã được chấp nhận.\n" +
                        "                        \n" +
                        "                        Sau khi xem xét và đánh giá hồ sơ của quý vị, chúng tôi rất vui mừng thông báo rằng quý vị đã đủ điều kiện tham gia. Chúng tôi sẽ liên hệ với quý vị để thông báo chi tiết về các bước tiếp theo và các thông tin liên quan.\n" +
                        "                        \n" +
                                               "Chúng tôi mong muốn được hợp tác và chào đón quý vị tham gia chương trình của chúng tôi.\n"+

                                               "Xin chân thành cảm ơn quý vị đã quan tâm và đăng ký tham gia.\n"+
                        "vui lòng đăng nhập để xử dụng dịch vụ \n"+
                        "http://localhost:8080/login"
        );

        // Chuyển hướng về trang danh sách chủ nhà chờ duyệt
        return "redirect:/admin/waiting-owners";
    }

    // Refuse waiting owner
    @GetMapping("/waiting-owners/refuse/{id}")
    public String refuseOwner(@PathVariable("id") int id) {
        WaitingOwner waitingOwner = waitingOwnerService.findById(id);
        waitingOwnerService.refuseWaitingOwner(id);
        String email = waitingOwner.getEmail();
        emailService.sendEmail(
                email,
                "Đăng ký làm chủ nhà bị từ chối",

                        "Chúng tôi rất tiếc phải thông báo rằng đơn đăng ký của quý vị vào không được chấp nhận.\n" +
                                "\n" +
                                "Sau khi xem xét kỹ lưỡng hồ sơ của quý vị, chúng tôi tiếc phải thông báo rằng quý vị không đủ điều kiện tham gia trong đợt này. Quyết định này dựa trên các tiêu chí và yêu cầu cụ thể của chương trình, và chúng tôi rất tiếc rằng quý vị không đáp ứng đủ các tiêu chí đó.\n" +
                                "\n" +
                                "Chúng tôi rất trân trọng sự quan tâm và nỗ lực của quý vị, và hy vọng sẽ có cơ hội làm việc cùng quý vị trong các chương trình sau này.\n" +
                                "\n" +
                                "Nếu quý vị có bất kỳ câu hỏi nào hoặc cần thêm thông tin, xin vui lòng liên hệ với chúng tôi qua số điện thoại hoặc email dưới đây.\n"+
                                "email: farmer365421@gmail.com\n"+
                                "số điện thoại : 12345567"

        );
        return "redirect:/admin/waiting-owners";
    }

    @GetMapping("/owners")
    public String listOwners(Model model) {
        List<User> owners = userService.getAllOwners();
        model.addAttribute("owners", owners); // Đảm bảo biến "owners" được truyền
        return "admin/owner-list";
    }

}