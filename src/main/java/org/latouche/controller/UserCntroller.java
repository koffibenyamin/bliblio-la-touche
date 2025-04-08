package org.latouche.controller;

import java.security.SecureRandom;
import java.util.List;

import org.latouche.dto.CreateUserRequest;
import org.latouche.model.User;
import org.latouche.repository.UserRepository;
import org.latouche.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserCntroller {
	
	public final UserService userService;
	
	private static final SecureRandom random = new SecureRandom();
	
	
	
	public UserCntroller(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("/all")
	public List<User> getAllUser(){
		return userService.getAll();
		
	}
	
	@GetMapping("search/{name}")
	public List<User> searchUser(@PathVariable String name){
		return userService.searchUser(name);
		
	}
	
	 @GetMapping("/serial")
	 public String generateSerialNumber() {
		 int serial = 10000 + random.nextInt(90000);
	     return String.valueOf(serial);
	    }
	
	@PostMapping("/save")
	public User save(@RequestBody User request) {
		return userService.saveUser(request);
	}
	
	@PutMapping("/update/{id}")
    public User updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
		
		return userService.updateUserinfo(id, user);
	}
	
	@DeleteMapping("/{id}")	
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		
		return userService.deleteUser(id);
	}
	
	@PutMapping("/reset/{id}")
    public User reset(@PathVariable Long id) {
		
		return userService.resetPass(id);
	}
	
	
	
	
	/*@PostMapping("/save1")
	public User save(@RequestBody User request) {
		return userService.SaveUserr(request);
	}
	
	@GetMapping("/{mot}")
	public List<User> searchUser(@PathVariable("mot") String mot){
		return userRepo.findByName_personContainingIgnoreCaseOrFirst_name_personContainingIgnoreCase(mot,mot);
		
	}*/
}
