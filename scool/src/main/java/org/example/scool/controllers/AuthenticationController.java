package org.example.scool.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenticationController {

    @GetMapping("/login")
    public String loginPage(Model model) {
        // Add any custom login page logic here (e.g., error messages)
        return "auth/login";  // Pointing to login.html in the `auth` directory
    }


}
