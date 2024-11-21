package org.example.profilecase5.Controller;

import org.example.profilecase5.Model.House;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Service.HouseService;
import org.example.profilecase5.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/house")
public class HouseController {

    @Autowired
    private HouseService houseService;

    @Autowired
    private UserService userService; // Service to retrieve user info

    // Show form to add a house
    @GetMapping("/new")
    public String showHouseForm(Model model) {
        model.addAttribute("house", new House());
        return "house/house_form"; // Return the view for house_form.html
    }

    // Process and save house data
    @PostMapping("/new")
    public String saveHouse(@Valid @ModelAttribute("house") House house, BindingResult bindingResult, Model model) {
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            // Add errors to model to display on form
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "house/house_form"; // Return the form with errors
        }

        // Get the username of the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Get the username of the current user

        // Find the user by username
        User user = userService.getUserByUsername(username);  // Assuming userService has this method

        // Set the user object into the house object
        house.setUser(user); // Set the whole user object, not just userId

        // Save the house object into the database
        houseService.saveHouse(house);

        // Redirect to the list of houses after saving
        return "redirect:/hosting/listings"; // Redirect to houses list
    }
}
