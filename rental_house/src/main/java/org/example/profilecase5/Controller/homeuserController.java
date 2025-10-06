package org.example.profilecase5.Controller;

import org.example.profilecase5.Model.HouseImage;
import org.example.profilecase5.Service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/main")
public class homeuserController {

    @Autowired
    private HouseService houseService;

    @GetMapping("")
    public String getAccountPage(Model model, Authentication authentication,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "9") int size) {

        Pageable pageable = PageRequest.of(page, size);  // Setting page and size
        Page<HouseImage> mainImages = houseService.getMainImages(pageable); // Get paginated images

        model.addAttribute("mainImages", mainImages);

        // Pagination details
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", mainImages.getTotalPages());
        model.addAttribute("totalItems", mainImages.getTotalElements());

        // Banner data
        List<String> banners = List.of(
                "/images/banner1.png",
                "/images/banner2.png",
                "/images/banner3.png"
        );
        model.addAttribute("banners", banners);

        return "main/home"; // Ensure you return the correct view
    }
}
