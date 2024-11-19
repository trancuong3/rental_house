package org.example.profilecase5.Controller;

import org.example.profilecase5.Model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/main")
public class mainController {
    @GetMapping("")
    public String getAccountPage() {
            return "main/home";
    }
}
