package org.example.profilecase5.Controller.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
public class MainController {

    @RequestMapping("/")
    public String home() {
        return "hosting";
    }


    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }
}