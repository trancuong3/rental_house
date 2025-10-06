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
        // Validate authentication object
        if (authentication == null || authentication.getName() == null) {
            model.addAttribute("error", "User not authenticated");
            return "error";
        }

        String username = authentication.getName();
        User user = userService.getUserByUsername(username);

        // Validate the retrieved user
        if (user == null) {
            model.addAttribute("error", "User not found");
            return "error";
        }

        // Fetch paginated house listings for the user
        Page<House> housePage = houseService.getHousesByUserId(user.getUserId(), page, 6);
        model.addAttribute("user", user);
        model.addAttribute("houses", housePage.getContent());
        model.addAttribute("currentPage", page);

        int totalHouses = (int) housePage.getTotalElements();
        int pageSize = housePage.getSize(); // Use the actual page size from the Page object
        int totalPages = housePage.getTotalPages();

        model.addAttribute("totalHouses", totalHouses);
        model.addAttribute("totalPages", totalPages);

        return "/hosting/listings";  // Return the view for the listings page
    }
}
