package com.example.library.controller;

import com.example.library.model.Shelf;
import com.example.library.service.ShelfService;
import org.springframework.lang.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/shelves")
@CrossOrigin
public class ShelfController {
    private final ShelfService shelfService;
    public ShelfController(ShelfService shelfService) { this.shelfService = shelfService; }

    @GetMapping
    public List<Shelf> all() { return shelfService.all(); }

    @GetMapping("/{id}")
    public ResponseEntity<Shelf> get(@PathVariable @NonNull Long id) {
        return shelfService.get(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Shelf create(@RequestBody @NonNull Shelf s) { return shelfService.save(s); }

    @PutMapping("/{id}")
    public ResponseEntity<Shelf> update(@PathVariable @NonNull Long id, @RequestBody Shelf s) {
        return shelfService.get(id).map(existing -> {
            existing.setName(s.getName());
            existing.setLocation(s.getLocation());
            existing.setCapacity(s.getCapacity());
            return ResponseEntity.ok(shelfService.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Long id) {
        shelfService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
