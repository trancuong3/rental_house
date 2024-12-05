package org.example.profilecase5.Controller;

import org.example.profilecase5.Model.House;
import org.example.profilecase5.Model.HouseImage;
import org.example.profilecase5.Model.RentalHistory;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Service.HouseService;
import org.example.profilecase5.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private HouseService houseService;

    @GetMapping("")
    public String getAccountPage(Model model, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.getUserByUsername(username);

            if (user == null) {
                model.addAttribute("errorMessage", "User not found");
                model.addAttribute("exceptionDetails", "No user found for username: " + username);
                return "error";  // Chuyển hướng đến trang lỗi nếu không tìm thấy người dùng
            }

            model.addAttribute("user", user);

            List<HouseImage> mainImages = houseService.getMainImages();
            model.addAttribute("mainImages", mainImages);

            return "home/home";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred while loading the account page.");
            model.addAttribute("exceptionDetails", getStackTrace(e));  // Thêm chi tiết lỗi vào Model
            return "error";  // Chuyển hướng đến trang lỗi
        }
    }

    private String getStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    @GetMapping("/detail/{id}")
    public String showDetail(@PathVariable("id") Integer id, Model model,Authentication authentication) {
        House house = houseService.getHouseById(id);
        List<HouseImage> images = houseService.getImagesByHouseId(id);
        String username = authentication.getName();  // Lấy username của người dùng hiện tại
        User user = userService.getUserByUsername(username);
        // Kiểm tra nếu người dùng tồn tại
        if (user != null) {
            model.addAttribute("user", user);  // Truyền người dùng vào model
            List<House> houses = houseService.getHousesByUserId(user.getUserId());  // Lấy danh sách nhà của người dùng
            model.addAttribute("houses", houses);
        } else {
            model.addAttribute("error", "User not found");
        }


        model.addAttribute("house", house);
        model.addAttribute("images", images);

        return "detail/detail";
    }

    @GetMapping("/history")
    public String userDetails(Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();

            User user = userService.getUserByUsername(currentUsername);
            if (user == null) {
                model.addAttribute("errorMessage", "User not found with username: " + currentUsername);
                model.addAttribute("exceptionDetails", "Could not find user for username: " + currentUsername);
                return "error"; // Nếu không tìm thấy người dùng, trả về trang lỗi
            }

            Set<RentalHistory> rentalHistories = user.getRentalHistories();
            model.addAttribute("user", user);
            model.addAttribute("rentalHistories", rentalHistories);
            double totalSpent = rentalHistories.stream().mapToDouble(RentalHistory::getTotalCost).sum();
            model.addAttribute("totalSpent", totalSpent);
            return "home/history";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred while loading the rental history.");
            model.addAttribute("exceptionDetails", getStackTrace(e));  // Thêm chi tiết lỗi vào Model
            return "error"; // Nếu có lỗi, trả về trang lỗi
        }
    }
}
