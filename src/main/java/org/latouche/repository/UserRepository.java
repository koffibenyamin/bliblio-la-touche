package org.latouche.repository;

import java.util.List;
import java.util.Optional;

import org.latouche.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

	//List<User> findByName_personContainingIgnoreCaseOrFirst_name_personContainingIgnoreCase(String mot, String mot2);
	
	List<User> findByNamePersonContainingIgnoreCaseOrFirstNamePersonContainingIgnoreCase(String name1,String name2);

	Optional<User> findByUsername(String adminUsername);

}
