package com.example.library.controller;

import com.example.library.model.Book;
import com.example.library.model.Shelf;
import com.example.library.service.BookService;
import com.example.library.service.ShelfService;
import com.example.library.dto.BookRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin
public class BookController {
    private final BookService bookService;
    private final ShelfService shelfService;

    public BookController(BookService bookService, ShelfService shelfService) {
        this.bookService = bookService;
        this.shelfService = shelfService;
    }

    @GetMapping
    public List<Book> all(@RequestParam(required=false) String title,
                          @RequestParam(required=false) String author,
                          @RequestParam(required=false) String isbn,
                          @RequestParam(required=false) Long shelfId) {
        if (title != null) return bookService.findByTitle(title);
        if (author != null) return bookService.findByAuthor(author);
        if (isbn != null) return bookService.findByIsbn(isbn);
        if (shelfId != null) return bookService.findByShelfId(shelfId);
        return bookService.all();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> get(@PathVariable Long id) {
        try { return ResponseEntity.ok(bookService.get(id)); }
        catch(Exception ex) { return ResponseEntity.notFound().build(); }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody BookRequest req) {
        try {
            Book b = new Book();
            b.setTitle(req.getTitle());
            b.setAuthor(req.getAuthor());
            b.setIsbn(req.getIsbn());
            if (req.getShelfId() != null) {
                Shelf s = shelfService.get(req.getShelfId()).orElseThrow(() -> new RuntimeException("Shelf not found"));
                b.setShelf(s);
            }
            return ResponseEntity.ok(bookService.create(b));
        } catch(Exception ex) { return ResponseEntity.badRequest().body(ex.getMessage()); }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody BookRequest req) {
        try {
            Book b = new Book();
            b.setTitle(req.getTitle());
            b.setAuthor(req.getAuthor());
            b.setIsbn(req.getIsbn());
            if (req.getShelfId() != null) {
                Shelf s = shelfService.get(req.getShelfId()).orElseThrow(() -> new RuntimeException("Shelf not found"));
                b.setShelf(s);
            }
            return ResponseEntity.ok(bookService.update(id, b));
        } catch(Exception ex) { return ResponseEntity.badRequest().body(ex.getMessage()); }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/move")
    public ResponseEntity<?> move(@PathVariable Long id, @RequestParam Long shelfId) {
        try { return ResponseEntity.ok(bookService.move(id, shelfId)); }
        catch(Exception ex) { return ResponseEntity.badRequest().body(ex.getMessage()); }
    }
}
