package org.latouche.service;

import java.util.List;
import java.util.Optional;

import org.latouche.dto.CreateUserRequest;
import org.latouche.model.Role;
import org.latouche.model.User;
import org.latouche.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class UserService {

	
	private final UserRepository userRepo;
	
	
	@PostConstruct
    public void initAdminUser() {
        String adminUsername = "admin";
        Optional<User> existingAdmin = userRepo.findByUsername(adminUsername);

        if (existingAdmin.isEmpty()) {
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setPassword("admin123"); // Default password
            admin.setRole(admin.role.Administrateur);

            userRepo.save(admin);
            System.out.println("✅ Default admin user created!");
        } else {
            System.out.println("ℹ️ Admin user already exists.");
        }
    }

	public UserService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}
	
	public List<User> getAll(){
		return userRepo.findAll();
	}
	
	public User saveUser(User request) {
		User user = new User(
				request.getNamePerson(),
				request.getFirstNamePerson(),
				request.getDateOfBirth(),
				request.getGender(),
				request.getPhoneNumber(),
				request.getEmail(),
				request.getRole()
				);
		return userRepo.save(user);
				
	}


	
	public Optional<User> findUser(long id){
		return userRepo.findById(id);
	}
	
	public List<User> searchUser(String name){
		return userRepo.findByNamePersonContainingIgnoreCaseOrFirstNamePersonContainingIgnoreCase( name, name);
	}
	
	public User updateUserinfo(long id, User user) {
		
		User userInfo = userRepo.findById(id).get();
		userInfo.setNamePerson(user.getNamePerson());
		userInfo.setFirstNamePerson(user.getFirstNamePerson());
		userInfo.setGender(user.getGender());
		userInfo.setRole(user.getRole());
		userInfo.setDateOfBirth(user.getDateOfBirth());
		userInfo.setEmail(user.getEmail());
		userInfo.setPhoneNumber(user.getPhoneNumber());
		
		return userRepo.save(userInfo);
		
	}
	

public User changePass(long id, String word) {
		User userInfo =userRepo.findById(id).get();
		userInfo.setPassword(word);
		return userRepo.save(userInfo);
}

public User resetPass(long id){
		User userInfo =userRepo.findById(id).get();
		userInfo.setPassword(userInfo.getUsername());
		return userRepo.save(userInfo);
	
}

public ResponseEntity<?> deleteUser(long id) {
	userRepo.deleteById(id);
	return ResponseEntity.ok().build();
	
} 

	
}
