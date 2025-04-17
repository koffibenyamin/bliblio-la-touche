package org.latouche.controller;

import java.security.Principal;
import java.security.SecureRandom;
import java.util.List;

import org.latouche.dto.CreateUserRequest;
import org.latouche.model.User;
import org.latouche.repository.UserRepository;
import org.latouche.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasRole('Administrateur')")
public class UserCntroller {
	
	public final UserService userService;
	private final PasswordEncoder passwordEncoder;
	
	private static final SecureRandom random = new SecureRandom();
	
	
	
	public UserCntroller(UserService userService, PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
	}
	
	@GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAll());
        model.addAttribute("newUser", new User());
        return "user";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("newUser") User user) {
        userService.saveUser(user);
        return "redirect:/user";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute("editUser") User user) {
        userService.updateUserinfo(user.getIdPerson(), user);
        return "redirect:/user";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/user";
    }

    @PostMapping("/reset/{id}")
    public String resetPassword(@PathVariable Long id) {
        userService.resetPass(id);
        return "redirect:/user";
    }

    @GetMapping("/edit/{id}")
    @ResponseBody
    public User getUserById(@PathVariable Long id) {
        return userService.findUser(id).orElse(null);
    }

    @GetMapping("/serial")
    @ResponseBody
    public String generateSerialNumber() {
        int serial = 10000 + random.nextInt(90000);
        return String.valueOf(serial);
    }
    
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(principal.getName());
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Mot de passe actuel incorrect");
            return "redirect:/settings";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Les mots de passe ne correspondent pas");
            return "redirect:/settings";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userService.saveUser(user);

        return "redirect:/logout";
    }

	

}
