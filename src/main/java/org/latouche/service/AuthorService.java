package org.latouche.service;

import java.util.List;
import java.util.Optional;

import org.latouche.model.Author;
import org.latouche.repository.AuthorRepository;
import org.springframework.stereotype.Service;

@Service 
public class AuthorService {
	private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    // Add a new author
    public Author addAuthor(Author author) {
        return authorRepository.save(author);
    }
    
    // Modify an existing author
    public Author updateAuthor(Long id, Author updatedAuthor) {
        Optional<Author> authorOptional = authorRepository.findById(id);
        if (authorOptional.isPresent()) {
            Author author = authorOptional.get();
            author.setName(updatedAuthor.getName());
            return authorRepository.save(author);
        }
        throw new IllegalArgumentException("Author not found");
    }

    // Get all authors
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    // Get author by ID
    public Optional<Author> getAuthorById(Long id) {
        return authorRepository.findById(id); 
    }
    
    public List<Author> getAuthorByName(String name) {
        return authorRepository.findByNameContainingIgnoreCase(name);
    }

    // Delete an author
    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }

}
