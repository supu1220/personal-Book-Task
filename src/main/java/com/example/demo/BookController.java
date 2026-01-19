package com.example.demo;

import com.example.demo.db.Book;
import com.example.demo.db.BookRepository;
import com.example.demo.google.GoogleBook;
import com.example.demo.google.GoogleBookService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
public class BookController {
    private final BookRepository bookRepository;
    private final GoogleBookService googleBookService;

    @Autowired
    public BookController(BookRepository bookRepository, GoogleBookService googleBookService) {
        this.bookRepository = bookRepository;
        this.googleBookService = googleBookService;
    }
    @PostMapping("/google/{googleId}")
    public ResponseEntity<Book> addBook(@PathVariable String googleId){
        GoogleBook googleBook = googleBookService.getBookById(googleId);
        if(googleBook == null || googleBook.items() == null || googleBook.items().isEmpty()){
            return ResponseEntity.badRequest().build();
        }
     GoogleBook.Item item = googleBook.items().get(0);
        GoogleBook.VolumeInfo volumeInfo = item.volumeInfo();
        if(volumeInfo == null){
            return ResponseEntity.badRequest().build();
        }
        String id = item.id();
        String title = volumeInfo.title();
        String authors = volumeInfo.authors().toString();
       Book book = new Book(id, title, authors);

       return new ResponseEntity<>(bookRepository.save(book), HttpStatus.CREATED);

    }

    @GetMapping("/books")
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/google")
    public GoogleBook searchGoogleBooks(@RequestParam("q") String query,
                                        @RequestParam(value = "maxResults", required = false) Integer maxResults,
                                        @RequestParam(value = "startIndex", required = false) Integer startIndex) {
        return googleBookService.searchBooks(query, maxResults, startIndex);
    }

    @GetMapping("/{googleId}")
    public GoogleBook getBookById(@PathVariable String googleId){
        return googleBookService.getBookById(googleId);
    }
}
