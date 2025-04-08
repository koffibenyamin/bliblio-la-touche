package org.latouche.service;

import java.util.List;
import java.util.Optional;


import org.latouche.model.Genre;

import org.latouche.repository.GenreRepository;
import org.springframework.stereotype.Service;

@Service
public class GenreService {
	private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    // Add a new genre
    public Genre addGenre(Genre genre) {
        return genreRepository.save(genre);
    }
    
    // Modify an existing genre
    public Genre updateGenre(Long id, Genre updatedGenre) {
        Optional<Genre> genreOptional = genreRepository.findById(id);
        if (genreOptional.isPresent()) {
            Genre genre = genreOptional.get();
            genre.setName(updatedGenre.getName());
            return genreRepository.save(genre);
        }
        throw new IllegalArgumentException("Author not found");
    }

    // Get all genres
    public List<Genre> getAllGenre() {
        return genreRepository.findAll();
    }

    // Get genre by ID
    public Optional<Genre> getGenreById(Long id) {
        return genreRepository.findById(id); 
    }
    
    // Get genre by name
    public List<Genre> getGenreByName(String name) {
        return genreRepository.findByNameContainingIgnoreCase(name);
    }

    // Delete an genre
    public void deleteGenre(Long id) {
        genreRepository.deleteById(id);
    }


}
