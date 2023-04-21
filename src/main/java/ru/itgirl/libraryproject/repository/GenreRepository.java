package ru.itgirl.libraryproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itgirl.libraryproject.model.Genre;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
