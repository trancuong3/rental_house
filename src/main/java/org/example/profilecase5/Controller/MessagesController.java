package org.example.profilecase5.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hosting/messages")
public class MessagesController {

    @GetMapping("")
    public String messages(Model model) {
        return "hosting/messages";
    }
}