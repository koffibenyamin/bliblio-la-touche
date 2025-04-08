package org.latouche.controller;

import java.util.List;
import java.util.Optional;


import org.latouche.model.Genre;
import org.latouche.service.GenreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/genre")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GenreController {
	private final GenreService genreService;

	public GenreController(GenreService genreService) {
		this.genreService = genreService;
	}
	
	 // Create a new genre
    @PostMapping
    public ResponseEntity<Genre> addGenre(@RequestBody Genre genre) {
        return ResponseEntity.ok(genreService.addGenre(genre));
    }

    // Update an existing genre
    @PutMapping("/{id}")
    public ResponseEntity<Genre> updateGenre(@PathVariable Long id, @RequestBody Genre genre) {
        return ResponseEntity.ok(genreService.updateGenre(id, genre));
    }

    // Get all genres
    @GetMapping
    public ResponseEntity<List<Genre>> getAllGenre() {
        return ResponseEntity.ok(genreService.getAllGenre());
    }

    // Get an genre by ID
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Genre>> getGenreById(@PathVariable Long id) {
        return ResponseEntity.ok(genreService.getGenreById(id));
    }
    
 // Get an genre by name
    @GetMapping("/{name}")
    public ResponseEntity<List<Genre>> getGenreByName(@PathVariable String name ) {
        return ResponseEntity.ok(genreService.getGenreByName(name));
    }

    // Delete an genre
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.ok("Genre deleted successfully");
    }
	

}
