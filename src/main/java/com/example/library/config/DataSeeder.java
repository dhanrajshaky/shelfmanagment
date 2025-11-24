package com.example.library.config;

import com.example.library.model.Book;
import com.example.library.model.Shelf;
import com.example.library.service.BookService;
import com.example.library.service.ShelfService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {
    private final ShelfService shelfService;
    private final BookService bookService;

    public DataSeeder(ShelfService shelfService, BookService bookService) {
        this.shelfService = shelfService;
        this.bookService = bookService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Seed only when empty (helps H2 demo runs and first-time MySQL runs)
        if (shelfService.all().isEmpty()) {
            Shelf s1 = new Shelf("Fiction A", "Floor 1 - Aisle 1", 100);
            Shelf s2 = new Shelf("Non-Fiction B", "Floor 1 - Aisle 2", 80);
            Shelf s3 = new Shelf("Reference", "Floor 2 - Aisle 1", 40);
            s1 = shelfService.save(s1);
            s2 = shelfService.save(s2);
            s3 = shelfService.save(s3);

            Book b1 = new Book("The Hobbit", "J.R.R. Tolkien", "9780000000001", s1);
            Book b2 = new Book("Clean Code", "Robert C. Martin", "9780132350884", s2);
            Book b3 = new Book("Encyclopedia of Things", "Various", "9781111111111", s3);
            bookService.create(b1);
            bookService.create(b2);
            bookService.create(b3);
        }
    }
}
