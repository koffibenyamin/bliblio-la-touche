package org.latouche.repository;

import java.util.List;
import java.util.Optional;

import org.latouche.dto.TopOrderedBookDTO;
import org.latouche.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface BookRepository extends JpaRepository<Book, Long> {
    
	Optional<Book> findBySerialNumber(String serialNumber);
	
	List<Book> findBySerialNumberIn(List<String> serialNumbers);
	
	@Query("SELECT b.id, b.serialNumber, b.title, b.numberOfCopies, b.description, b.thumbnailPath, " +
		       "COALESCE(ge.name, 'inconnu'), COALESCE(au.name, 'inconnu') " +
		       "FROM Book b " +
		       "LEFT JOIN b.genre ge " +  
		       "LEFT JOIN b.author au " +
		       "ORDER BY b.id ASC")
    List<Object[]> findBooksWithDetails();
    
    @Query("SELECT new org.latouche.dto.TopOrderedBookDTO(b.id, b.title, b.thumbnailPath) " +
    	       "FROM Order o JOIN o.books b " +
    	       "GROUP BY b.id, b.title, b.thumbnailPath " +
    	       "ORDER BY COUNT(b.id) DESC")
    	List<TopOrderedBookDTO> findTop7OrderedBooks();
    
    boolean existsBySerialNumber(String serialNumber);
    
    

}


