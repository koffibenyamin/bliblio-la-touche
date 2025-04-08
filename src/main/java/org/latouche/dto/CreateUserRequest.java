package org.latouche.dto;

import java.util.Date;

import org.latouche.model.Gender;
import org.latouche.model.Role;


import lombok.Data;

@Data
public class CreateUserRequest {
	
	public String name_person;
	public String first_name_person;
	public Date date_of_birth;
	public Gender gender;
	public String phone_number;
	public String email;
	public String username;
	public String password;
	public Role role;
	
	
	public CreateUserRequest(String name_person, String first_name_person, Date date_of_birth, Gender gender,
			String phone_number, String email, String username, String password, Role role) {
		this.name_person = name_person;
		this.first_name_person = first_name_person;
		this.date_of_birth = date_of_birth;
		this.gender = gender;
		this.phone_number = phone_number;
		this.email = email;
		this.username = username;
		this.password = password;
		this.role = role;
	}
	
	
	

}
