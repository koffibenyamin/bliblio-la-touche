package org.latouche.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.latouche.dto.BookDTO;
import org.latouche.dto.BorrowInfoDTO;
import org.latouche.dto.TopOrderedBookDTO;
import org.latouche.model.Author;
import org.latouche.model.Book;
import org.latouche.parameters.QRCodeGenerator;
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
import com.google.zxing.WriterException;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@Controller
@RequestMapping("book")
@RequiredArgsConstructor
public class BookController {
	
	  private final BookService bookService;
	  private final BookRepository bookRepository;
	  private final GenreRepository genreRepository;
	  private final AuthorRepository authorRepository;
	  @Autowired
	  private QRCodeGenerator qrCodeGenerator;
	  
	  
	  

	   
	    
	    @GetMapping("")
	    public String listBooks(Model model) {
	        List<BookDTO> books = bookService.getBooksWithAvailability();
	        List<TopOrderedBookDTO> topOrderedBooks = bookService.getTop7OrderedBooks();
	        model.addAttribute("bk", books);
	        model.addAttribute("topBooks", topOrderedBooks);
	        model.addAttribute("authors", authorRepository.findAll());
	        model.addAttribute("genres", genreRepository.findAll());
	        return "book";
	    }
	    
	    @GetMapping("/details/{bookId}")
	    public String getBookDetailsWithBorrowInfo(@PathVariable Long bookId, Model model) {
	        // Get BookDTO
	        BookDTO bookDTO = bookService.getBookDTOById(bookId);
	        
	        // Get list of members who borrowed this book
	        List<BorrowInfoDTO> borrowList = bookService.getBorrowInfoByBookId(bookId);

	        model.addAttribute("book", bookDTO);
	        model.addAttribute("borrowList", borrowList);

	        return "book-info"; // name of your Thymeleaf page (book-details.html)
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
	    
	    @GetMapping("/get/{id}")
	    @ResponseBody
	    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
	    	return bookRepository.findById(id).map(book -> {
	            BookDTO dto = new BookDTO();
	            dto.setId(book.getId());
	            dto.setSerialNumber(book.getSerialNumber());
	            dto.setTitle(book.getTitle());
	            dto.setDescription(book.getDescription());
	            dto.setThumbnailPath(book.getThumbnailPath());
	            dto.setNumberOfCopies(book.getNumberOfCopies());
	            dto.setAuthorName(book.getAuthor() != null ? book.getAuthor().getName() : "");
	            dto.setGenreName(book.getGenre() != null ? book.getGenre().getName() : "");
	            return ResponseEntity.ok(dto);
	        }).orElse(ResponseEntity.notFound().build());
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
	    
	    @GetMapping("/qr/{serialNumber}")
	    public ResponseEntity<byte[]> generateQRCode(@PathVariable String serialNumber) throws IOException, WriterException {
	        BufferedImage qrImage = qrCodeGenerator.generateQRCodeImage(serialNumber, 100, 100);

	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        ImageIO.write(qrImage, "PNG", baos);

	        byte[] imageBytes = baos.toByteArray();
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.IMAGE_PNG);

	        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
	    }
	    
	    @GetMapping("/preview/{serialNumber}")
	    @ResponseBody
	    public ResponseEntity<BookDTO> getBookPreview(@PathVariable String serialNumber) {
	        Book book = bookRepository.findBySerialNumber(serialNumber)
	            .orElseThrow(() -> new RuntimeException("Book not found"));

	        BookDTO dto = new BookDTO(book.getSerialNumber(), book.getTitle(), book.getThumbnailPath());
	        return ResponseEntity.ok(dto);
	    }
	    
	    
	    
	   

}
