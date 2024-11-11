package org.example.profilecase5.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hosting")
public class HostingController {

    @GetMapping("")
    public String showHostingPage(){
        return "hosting/hosting";
    }

}