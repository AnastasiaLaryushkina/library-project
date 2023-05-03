package ru.itgirl.libraryproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.itgirl.libraryproject.model.Book;
import ru.itgirl.libraryproject.model.Genre;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    List<Book> findByGenre (Genre genre);

    Optional<Book> findBookByName(String name);

    @Query(nativeQuery = true, value = "Select * from book where name = ?")
    Optional<Book> findBookByNameBySql(String name);
}
