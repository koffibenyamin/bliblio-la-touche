package org.latouche.model;

import java.time.LocalDate;
import java.util.Random;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "id_member")
public class Member extends Person{
	
	@Column(unique = true)
	public String registerNumber;
	
	@Enumerated(EnumType.STRING)
	public Status status;

	public Member(String namePerson, String firstNamePerson, LocalDate dateOfBirth, Gender gender, String phoneNumber,
			String email, String registerNumber, Status status) {
		super(namePerson, firstNamePerson, dateOfBirth, gender, phoneNumber, email);
		this.registerNumber = registerNumber;
		this.status = status;
	}
	
	
	@PrePersist
    public void generateRegisterNumber() {
        if (this.registerNumber == null) {
            Random random = new Random();
            int number = random.nextInt(10000); // Generate a number between 0 and 9999
            this.registerNumber = String.format("M%04d", number); // Format as "M1234"
        }
    }
	
	

}
