package org.latouche.repository;

import java.util.List;
import java.util.Optional;

import org.latouche.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
	
	Optional<Author> findByName(String name);
	List<Author> findByNameContainingIgnoreCase(String name);

}
