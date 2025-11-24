package com.example.library.service;

import com.example.library.model.Shelf;
import com.example.library.repository.ShelfRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ShelfService {
    private final ShelfRepository shelfRepo;

    public ShelfService(ShelfRepository shelfRepo) {
        this.shelfRepo = shelfRepo;
    }

    public List<Shelf> all() { return shelfRepo.findAll(); }
    public Optional<Shelf> get(Long id) { return shelfRepo.findById(id); }
    public Shelf save(Shelf s) { return shelfRepo.save(s); }
    public void delete(Long id) { shelfRepo.deleteById(id); }
}
