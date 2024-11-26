package org.example.profilecase5.Controller;

import org.example.profilecase5.Model.HouseImage;
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
@RequestMapping("/main")
public class homeuserController {
    @Autowired
    private UserService userService;
    @Autowired
    private HouseService houseService;
    @GetMapping("")
    public String getAccountPage(Model model, Authentication authentication) {

        List<HouseImage> mainImages = houseService.getMainImages();
        model.addAttribute("mainImages", mainImages);

        return "main/home";
    }
}
