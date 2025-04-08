package org.latouche.repository;

import java.util.List;
import java.util.Optional;


import org.latouche.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
	Optional<Genre> findByName(String name);
	List<Genre> findByNameContainingIgnoreCase(String name);
}
