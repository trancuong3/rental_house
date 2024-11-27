package org.example.profilecase5.Controller.hosting;

import org.example.profilecase5.Model.House;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Service.HouseService;
import org.example.profilecase5.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/hosting/listings")
public class ListingsController {

    @Autowired
    private UserService userService;

    @Autowired
    private HouseService houseService;

    @GetMapping("")
    public String getListingsPage(Model model, Authentication authentication) {
        String username = authentication.getName();  // Lấy tên người dùng từ Authentication
        User user = userService.getUserByUsername(username);  // Lấy người dùng từ dịch vụ

        // Kiểm tra nếu người dùng tồn tại
        if (user != null) {
            model.addAttribute("user", user);  // Truyền người dùng vào model
            List<House> houses = houseService.getHousesByUserId(user.getUserId());  // Lấy danh sách nhà của người dùng
            model.addAttribute("houses", houses);
        } else {
            model.addAttribute("error", "User not found");
        }

        return "/hosting/listings";  // Trả về trang listings
    }

}
