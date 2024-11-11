package org.example.profilecase5.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hosting/listings")
public class ListingsController {

    @GetMapping
    public String listings(Model model) {
        return "hosting/listings";
    }
}