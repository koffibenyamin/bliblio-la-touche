package org.latouche.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data 
@NoArgsConstructor
@AllArgsConstructor
public class Book {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 5)
    private String serialNumber;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String thumbnailPath;

    private int numberOfCopies;

    @ManyToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;
    
    public boolean isAvailable() {
        return getAvailableCopies() > 0;
    }

    public int getAvailableCopies() {
        return numberOfCopies - getBorrowedCopies();
    }

    
    private int getBorrowedCopies() {
        
        return 0; 
    }

    public void borrowBook() {
        if (isAvailable()) {
            numberOfCopies--;
        } else {
            throw new RuntimeException("No copies available to borrow.");
        }
    }

    public void returnBook() {
        numberOfCopies++;
    }

  
}
	