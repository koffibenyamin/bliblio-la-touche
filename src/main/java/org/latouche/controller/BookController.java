package org.latouche.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.latouche.dto.BookDTO;
import org.latouche.model.Author;
import org.latouche.model.Book;
import org.latouche.repository.AuthorRepository;
import org.latouche.repository.BookRepository;
import org.latouche.repository.GenreRepository;
import org.latouche.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.engine.AttributeName;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@Controller
@RequestMapping("book")
@RequiredArgsConstructor
public class BookController {
	
	  private final BookService bookService;
	  private final GenreRepository genreRepository;
	  private final AuthorRepository authorRepository;
	  
	  @GetMapping("/{bookId}/availability")
	  public ResponseEntity<Boolean> checkBookAvailability(@PathVariable Long bookId) {
	        boolean available = bookService.isBookAvailable(bookId);
	        return ResponseEntity.ok(available);
	    }
	  
	  

	  @GetMapping("/isbn/{isbn}")
	  public ResponseEntity<Book> fetchBookByISBN(
	            @PathVariable String isbn, 
	            @RequestParam(defaultValue = "1") int numberOfCopies) {
	        Book book = bookService.fetchAndSaveBookByISBN(isbn, numberOfCopies);
	        return ResponseEntity.ok(book);
	    }
	  
	  @PostMapping
	  public ResponseEntity<Book> createBooks(
	          @ModelAttribute Book book,
	          @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnailFile) {
	      
	      Book savedBook = bookService.createBook(book, thumbnailFile);
	      return ResponseEntity.ok(savedBook);
	  }

	  
	  @PutMapping("/{id}")
	    public ResponseEntity<Book> updateBooks(
	            @PathVariable Long id,
	            @ModelAttribute Book updatedBook,
	            @RequestParam(value = "thumbnail", required = false) MultipartFile newThumbnail) {
	        Book book = bookService.updateBook(id, updatedBook, newThumbnail);
	        return ResponseEntity.ok(book);
	    }
	  
	

	    @DeleteMapping("/{id}")
	    public ResponseEntity<String> deleteBooks(@PathVariable Long id) {
	        bookService.deleteBook(id);
	        return ResponseEntity.ok("Book deleted successfully");
	    }

	   /// @GetMapping
	    //public List<Map<String, Object>> getAllBooks() {
	   //     return bookService.getAllBooks();
	    ///}
	    
	    @GetMapping("")
	    public String getall(Model model) {
	    	List<Map<String, Object>> books= bookService.getAllBooks();
	    	
	    	
	    	
	    	model.addAttribute("bk", books);	    	
	    	model.addAttribute("authors", authorRepository.findAll());
	        model.addAttribute("genres", genreRepository.findAll());
	    	return "book"; 
	    }
	    
	    @GetMapping("/isbn")
	    public String fetchBookByISBN(
	            @RequestParam("isbn") String isbn,
	            @RequestParam(defaultValue = "1") int numberOfCopies,
	            RedirectAttributes redirectAttributes) {
	        
	        try {
	            Book book = bookService.fetchAndSaveBookByISBN(isbn, numberOfCopies);
	            redirectAttributes.addFlashAttribute("message", "Livre Enrégistrer avec succès: " + book.getSerialNumber());
	        } catch (Exception e) {
	            redirectAttributes.addFlashAttribute("error", "Livre non retrouvé, essayé manuellement: " + e.getMessage());
	        }

	        return "redirect:/book";
	    }	    
	    
	    
	    @PostMapping("/add")
	    public String createBook(@ModelAttribute Book book,
	                             @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnailFile) {
	        bookService.createBook(book, thumbnailFile);
	        
	        
	        return "redirect:/book?success=true";
	    }
	    
	    
	    @PostMapping("/edit/{id}")
	    public String updateBook(@PathVariable Long id,
	                             @ModelAttribute("bookForm") Book updatedBook,
	                             @RequestParam(value = "thumbnail", required = false) MultipartFile newThumbnail) {
	        bookService.updateBook(id, updatedBook, newThumbnail);
	        return "redirect:/book";
	    }

	    @GetMapping("/delete/{id}")
	    public String deleteBook(@PathVariable Long id) {
	        bookService.deleteBook(id);
	        return "redirect:/book";
	    }
	    
	   

}
