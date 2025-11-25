package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.model.Shelf;
import com.example.library.repository.BookRepository;
import com.example.library.repository.ShelfRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepo;
    private final ShelfRepository shelfRepo;

    public BookService(BookRepository bookRepo, ShelfRepository shelfRepo) {
        this.bookRepo = bookRepo;
        this.shelfRepo = shelfRepo;
    }

    public List<Book> all() { return bookRepo.findAll(); }
    public Book get(@NonNull Long id) { return bookRepo.findById(id).orElseThrow(() -> new RuntimeException("Book not found")); }

    public Book create(Book b) {
        if (b.getShelf() != null) {
            Long shelfId = b.getShelf().getId();
            if (shelfId != null) {
                Shelf s = shelfRepo.findById(shelfId).orElseThrow(() -> new RuntimeException("Shelf not found"));
                long current = bookRepo.countByShelfId(s.getId());
                if (current >= s.getCapacity()) throw new RuntimeException("Shelf is full");
                b.setShelf(s);
            }
        }
        Book saved = bookRepo.save(b);
        // maintain bidirectional relationship in memory / DB
        if (saved.getShelf() != null) {
            Shelf s = saved.getShelf();
            if (!s.getBooks().contains(saved)) {
                s.getBooks().add(saved);
                shelfRepo.save(s);
            }
        }
        return saved;
    }

    public Book update(@NonNull Long id, Book updated) {
        Book ex = bookRepo.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
        ex.setTitle(updated.getTitle());
        ex.setAuthor(updated.getAuthor());
        ex.setIsbn(updated.getIsbn());
        if (updated.getShelf() != null) {
            Long shelfId = updated.getShelf().getId();
            if (shelfId != null) {
                Shelf s = shelfRepo.findById(shelfId).orElseThrow(() -> new RuntimeException("Shelf not found"));
                long current = bookRepo.countByShelfId(s.getId());
                if (!s.getId().equals(ex.getShelf() == null ? null : ex.getShelf().getId()) && current >= s.getCapacity())
                    throw new RuntimeException("Shelf is full");
                // remove from old shelf list if present
                Shelf old = ex.getShelf();
                ex.setShelf(s);
                Book saved = bookRepo.save(ex);
                if (old != null && old.getBooks().contains(saved)) {
                    old.getBooks().removeIf(b -> b.getId().equals(saved.getId()));
                    shelfRepo.save(old);
                }
                if (!s.getBooks().contains(saved)) {
                    s.getBooks().add(saved);
                    shelfRepo.save(s);
                }
                return saved;
            }
        } else {
            ex.setShelf(null);
        }
        return bookRepo.save(ex);
    }

    public void delete(@NonNull Long id) { bookRepo.deleteById(id); }

    public Book move(@NonNull Long bookId, @NonNull Long shelfId) {
        Book b = bookRepo.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        Shelf s = shelfRepo.findById(shelfId).orElseThrow(() -> new RuntimeException("Shelf not found"));
        long current = bookRepo.countByShelfId(s.getId());
        if (current >= s.getCapacity()) throw new RuntimeException("Target shelf is full");
        Shelf old = b.getShelf();
        b.setShelf(s);
        Book saved = bookRepo.save(b);
        if (old != null && old.getBooks().removeIf(book -> book.getId().equals(saved.getId()))) {
            shelfRepo.save(old);
        }
        if (!s.getBooks().contains(saved)) {
            s.getBooks().add(saved);
            shelfRepo.save(s);
        }
        return saved;
    }

    public List<Book> findByTitle(String q) { return bookRepo.findByTitleContainingIgnoreCase(q); }
    public List<Book> findByAuthor(String q) { return bookRepo.findByAuthorContainingIgnoreCase(q); }
    public List<Book> findByIsbn(String q) { return bookRepo.findByIsbn(q); }
    public List<Book> findByShelfId(Long shelfId) { return bookRepo.findByShelfId(shelfId); }
}
