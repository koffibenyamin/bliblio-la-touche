package org.latouche.model;
import java.time.LocalDate;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;




@Entity
@Data
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "id_user")
@Table(name = "user_")
public class User extends Person {
	
	public String username;
	public String password;
	@Enumerated(EnumType.STRING)
	public Role role;
	
	
	
	public User( String namePerson, String firstNamePerson, LocalDate dateOfBirth, Gender gender,
			String phoneNumber, String email, Role role) {
		super( namePerson, firstNamePerson, dateOfBirth, gender, phoneNumber, email);
		
		
		Date d = new Date();
		int numn=d.getYear()+d.getMonth()+d.getDay()+d.getHours()+d.getMinutes()+d.getSeconds();
		this.username=namePerson+String.valueOf(numn);
		this.password=namePerson+String.valueOf(numn);
		this.role=role;
	}



	public User(Long idPerson) {
		super(idPerson);
		
		
	}


	
	
	
}
