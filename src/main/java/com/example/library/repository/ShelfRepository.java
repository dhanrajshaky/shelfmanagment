package com.example.library.repository;

import com.example.library.model.Shelf;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Long> {
	@Override
	@EntityGraph(attributePaths = "books")
	List<Shelf> findAll();

	@Override
	@EntityGraph(attributePaths = "books")
	Optional<Shelf> findById(Long id);
}
