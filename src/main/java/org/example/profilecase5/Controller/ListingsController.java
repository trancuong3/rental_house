package org.example.profilecase5.Controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;

@Controller
@RequestMapping("/hosting/listings")
public class ListingsController {

    @Autowired
    private UserService userService;

    @Autowired
    private HouseService houseService;

    @GetMapping("")
    public String getListingsPage(Model model, Authentication authentication,
                                  @RequestParam(value = "page", defaultValue = "0") int page) {
        String username = authentication.getName();  // Get the username from Authentication
        User user = userService.getUserByUsername(username);  // Get the user from the service

        // Check if the user exists
        if (user != null) {
            model.addAttribute("user", user);  // Pass the user to the model
            Page<House> housePage = houseService.getHousesByUserId(user.getUserId(), page, 9);  // Get the user's houses with pagination
            model.addAttribute("houses", housePage.getContent());
            model.addAttribute("currentPage", page);

            int totalHouses = (int) housePage.getTotalElements();
            int pageSize = 9;
            int totalPages = (int) Math.ceil((double) totalHouses / pageSize);
            model.addAttribute("totalPages", totalPages);
        } else {
            model.addAttribute("error", "User not found");
        }

        return "/hosting/listings";  // Return the listings page
    }
}
