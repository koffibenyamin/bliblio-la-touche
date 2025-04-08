package org.latouche.model;
import java.time.LocalDate;
import java.util.Date;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data 
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED )
public abstract class Person {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long  idPerson;
	public String namePerson;
	public String firstNamePerson;
	public LocalDate dateOfBirth;
	
	@Enumerated(EnumType.STRING)
	public Gender gender;
	public String phoneNumber;
	public String email;
	
	public Person(String namePerson, String firstNamePerson, LocalDate dateOfBirth, Gender gender, String phoneNumber,
			String email) {
		this.namePerson = namePerson;
		this.firstNamePerson = firstNamePerson;
		this.dateOfBirth = dateOfBirth;
		this.gender = gender;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}

	public Person(Long idPerson2) {
		// TODO Auto-generated constructor stub
	}
	
	
}
