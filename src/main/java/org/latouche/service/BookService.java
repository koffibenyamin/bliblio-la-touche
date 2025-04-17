package org.latouche.service;

import org.latouche.dto.BookDTO;
import org.latouche.dto.BorrowInfoDTO;
import org.latouche.dto.TopOrderedBookDTO;
import org.latouche.model.Author;
import org.latouche.model.Book;
import org.latouche.model.Genre;
import org.latouche.repository.AuthorRepository;
import org.latouche.repository.BookRepository;
import org.latouche.repository.GenreRepository;
import org.latouche.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import org.json.JSONObject;
import org.json.JSONArray;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

@Service 
@RequiredArgsConstructor
public class BookService {
	 	private static final String GOOGLE_BOOKS_API = "https://www.googleapis.com/books/v1/volumes?q=isbn:";
	    private static final String THUMBNAIL_DIR = System.getProperty("user.dir") + "/src/main/resources/static/thumbnails/";
	    //System.getProperty("user.dir") + "/thumbnails/";

	    private final BookRepository bookRepository;
	    private final GenreRepository genreRepository;
	    private final AuthorRepository authorRepository;
	    private final OrderRepository orderRepository;
	    private final RestTemplate restTemplate;

	    public Book fetchAndSaveBookByISBN(String isbn, int numberOfCopies) {
	        String apiUrl = GOOGLE_BOOKS_API + isbn;
	        String response = restTemplate.getForObject(apiUrl, String.class);

	        if (response == null) {
	            throw new RuntimeException("Failed to fetch data from Google Books API");
	        }

	        JSONObject json = new JSONObject(response);
	        if (!json.has("items")) {
	            throw new RuntimeException("Book not found for ISBN: " + isbn);
	        }

	        JSONObject volumeInfo = json.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo");

	        String title = volumeInfo.optString("title", "Unknown Title");
	        String description = extractFrenchDescription(volumeInfo);
	        String thumbnailUrl = volumeInfo.has("imageLinks") ? volumeInfo.getJSONObject("imageLinks").optString("thumbnail", null) : null;
	        String authorName = volumeInfo.has("authors") ? volumeInfo.getJSONArray("authors").getString(0) : "Unknown Author";
	        String genreName = volumeInfo.has("categories") ? volumeInfo.getJSONArray("categories").getString(0) : "Unknown Genre";

	        Author author = authorRepository.findByName(authorName).orElseGet(() -> {
	            Author newAuthor = new Author();
	            newAuthor.setName(authorName);
	            return authorRepository.save(newAuthor);
	        });

	        Genre genre = genreRepository.findByName(genreName).orElseGet(() -> {
	            Genre newGenre = new Genre();
	            newGenre.setName(genreName);
	            return genreRepository.save(newGenre);
	        });

	        Book book = new Book();
	        String serialNumber = generateUniqueSerialNumber();
	        book.setSerialNumber(serialNumber);
	        String thumbnailPath = (thumbnailUrl != null) ? saveThumbnailFromUrl(thumbnailUrl, serialNumber) : null;
	        

	        
	        book.setTitle(title);
	        book.setDescription(description);
	        book.setThumbnailPath(thumbnailPath);
	        book.setNumberOfCopies(numberOfCopies);
	        book.setAuthor(author);
	        book.setGenre(genre);

	        return bookRepository.save(book);
	    }
	    
	    
	    
	    public Book createBook(Book book, MultipartFile thumbnailFile) {
	        // Ensure genre exists
	        Genre genre = genreRepository.findByName(book.getGenre().getName())
	                .orElseGet(() -> genreRepository.save(book.getGenre()));
	        book.setGenre(genre);

	        // Ensure author exists
	        Author author = authorRepository.findByName(book.getAuthor().getName())
	                .orElseGet(() -> authorRepository.save(book.getAuthor()));
	        book.setAuthor(author);

	        // Generate a unique 5-digit serial number
	        String serialNumber = generateUniqueSerialNumber();
	        book.setSerialNumber(serialNumber);

	        // Save thumbnail if provided
	        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
	            String thumbnailPath = saveThumbnail(thumbnailFile, serialNumber);
	            book.setThumbnailPath(thumbnailPath);
	        }

	        return bookRepository.save(book);
	    }

	    
	    
	    //


	    public Book updateBook(Long id, Book updatedBook, MultipartFile newThumbnail) {
	        return bookRepository.findById(id).map(book -> {
	            book.setTitle(updatedBook.getTitle());
	            book.setDescription(updatedBook.getDescription());
	            book.setNumberOfCopies(updatedBook.getNumberOfCopies());

	            if (updatedBook.getGenre() != null) {
	                Genre genre = genreRepository.findByName(updatedBook.getGenre().getName())
	                        .orElseGet(() -> genreRepository.save(updatedBook.getGenre()));
	                book.setGenre(genre);
	            }

	            if (updatedBook.getAuthor() != null) {
	                Author author = authorRepository.findByName(updatedBook.getAuthor().getName())
	                        .orElseGet(() -> authorRepository.save(updatedBook.getAuthor()));
	                book.setAuthor(author);
	            }

	            if (newThumbnail != null && !newThumbnail.isEmpty()) {
	                deleteThumbnail(book.getThumbnailPath());
	                String newPath = saveThumbnail(newThumbnail, book.getSerialNumber());
	                book.setThumbnailPath(newPath);
	            }

	            return bookRepository.save(book);
	        }).orElseThrow(() -> new RuntimeException("Book not found"));
	    }    
	    

	    
	    //

	    

	    public void deleteBook(Long id) {
	        bookRepository.findById(id).ifPresentOrElse(book -> {
	            deleteThumbnail(book.getThumbnailPath());
	            bookRepository.delete(book);
	        }, () -> {
	            throw new RuntimeException("Book not found");
	        });
	    }

	    public List<Map<String, Object>> getAllBooks() {
	        return bookRepository.findBooksWithDetails().stream()
	                .map(obj -> Map.of(
	                        "id", obj[0],
	                        "serialNumber", obj[1],
	                        "title", obj[2],
	                        "numberOfCopies", obj[3],
	                        "description", obj[4] != null ? obj[4] : "No Description Available",
	                        "thumbnailPath", obj[5] != null ? obj[5] : "No Thumbnail Available",
	                        "genre", obj[6],
	                        "author", obj[7]
	                ))
	                .collect(Collectors.toList());
	    }

	    private String extractFrenchDescription(JSONObject volumeInfo) {
	        return volumeInfo.has("description") ? volumeInfo.getString("description") : "Description non disponible en français.";
	    }

	    private String saveThumbnailFromUrl(String imageUrl, String serialNumber) {
	        try (InputStream in = new URL(imageUrl).openStream()) {
	            File directory = new File(THUMBNAIL_DIR);
	            if (!directory.exists()) {
	                directory.mkdirs();
	            }
	            String filePath = THUMBNAIL_DIR +  serialNumber + ".jpg";
	            try (FileOutputStream out = new FileOutputStream(filePath)) {
	                byte[] buffer = new byte[1024];
	                int bytesRead;
	                while ((bytesRead = in.read(buffer)) != -1) {
	                    out.write(buffer, 0, bytesRead);
	                }
	            }
	            return "/thumbnails/" +  serialNumber + ".jpg";
	        } catch (Exception e) {
	            return null;
	        }
	    }
	    
	    
	    private String saveThumbnail(MultipartFile file, String serialNumber) {
	        try {
	            File directory = new File(THUMBNAIL_DIR);
	            if (!directory.exists()) {
	                directory.mkdirs();
	            }
	            String filePath = THUMBNAIL_DIR + serialNumber + ".jpg";
	            file.transferTo(new File(filePath));
	            return "/thumbnails/" + serialNumber + ".jpg";
	        } catch (IOException e) {
	            throw new RuntimeException("Failed to save thumbnail", e);
	        }
	    }

	    private void deleteThumbnail(String filePath) {
	        if (filePath != null) {
	            File file = new File(filePath);
	            if (file.exists()) {
	                file.delete();
	            }
	        }
	    }

	   

    private String generateUniqueSerialNumber() {
        Random random = new Random();
        String serialNumber;
        do {
            serialNumber = String.format("%05d", random.nextInt(100000));
        } while (bookRepository.findBySerialNumber(serialNumber).isPresent());
        return serialNumber;
    }
    

    public boolean isBookAvailable(Long bookId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isEmpty()) {
            return false;
        }

        Book book = bookOpt.get();
        int borrowedCopies = orderRepository.countBorrowedCopies(bookId);
        return (book.getNumberOfCopies() - borrowedCopies) > 0;
    }
    
    public List<BookDTO> getBooksWithAvailability() {
        List<Object[]> rawBooks = bookRepository.findBooksWithDetails();
        
        return rawBooks.stream().map(obj -> {
            Long id = ((Number) obj[0]).longValue();
            String serialNumber = (String) obj[1];
            String title = (String) obj[2];
            int numberOfCopies = ((Number) obj[3]).intValue();
            String description = (String) obj[4];
            String thumbnailPath = (String) obj[5];
            String genreName = (String) obj[6];
            String authorName = (String) obj[7];

            int borrowed = orderRepository.countBorrowedCopies(id);
            int available = numberOfCopies - borrowed;

            return new BookDTO(
                id,
                serialNumber,
                title,
                numberOfCopies,
                description,
                thumbnailPath,
                genreName,
                authorName,
                available
            );
        }).toList();
    }
    
    public List<BorrowInfoDTO> getBorrowInfoByBookId(Long bookId) {
        List<Object[]> rows = orderRepository.findBorrowInfoByBookId(bookId);

        return rows.stream().map(row -> new BorrowInfoDTO(
            (Long) row[0],  // idPerson
            row[1] + " " + row[2], // full name: name + first name
            (String) row[3],  // register number
            (String) row[4],  // phone
            (String) row[5],  // email
            (LocalDate) row[6], // borrow date
            (LocalDate) row[7], // return date
            (Boolean) row[8]    // returned
        )).toList();
    }
    
    public BookDTO getBookDTOById(Long bookId) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found"));

        return new BookDTO(
            book.getId(),
            book.getSerialNumber(),
            book.getTitle(),
            book.getNumberOfCopies(),
            book.getDescription(),
            book.getThumbnailPath(),
            book.getGenre() != null ? book.getGenre().getName() : "",
            book.getAuthor() != null ? book.getAuthor().getName() : "",
            book.getAvailableCopies()
        );
    }
    
    public List<TopOrderedBookDTO> getTop7OrderedBooks() {
        return bookRepository.findTop7OrderedBooks()
                             .stream()
                             .limit(7)
                             .toList(); // limit to 7 manually
    }


    
}
