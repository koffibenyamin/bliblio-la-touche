package org.latouche.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
	
	@GetMapping("/login")
    public String loginPage() {
        return "login"; // This returns login.html from templates folder
    }

}
