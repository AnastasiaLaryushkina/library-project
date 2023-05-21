package ru.itgirl.libraryproject.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itgirl.libraryproject.dto.AuthorDto;
import ru.itgirl.libraryproject.dto.AuthorsAndBooksResponseDto;
import ru.itgirl.libraryproject.dto.BookDto;
import ru.itgirl.libraryproject.dto.GenreCreateDto;
import ru.itgirl.libraryproject.model.Author;
import ru.itgirl.libraryproject.model.Book;
import ru.itgirl.libraryproject.model.Genre;
import ru.itgirl.libraryproject.repository.BookRepository;
import ru.itgirl.libraryproject.repository.GenreRepository;

import java.util.*;




@Service
@RequiredArgsConstructor
@Slf4j
public class GenreServiceImpl implements GenreService {
    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;

    @Override
    public AuthorsAndBooksResponseDto getGenreById(Long id) {
        log.info("Try to find genre by id {}", id);
        Optional<Genre> genre = genreRepository.findById(id);
        if (genre.isPresent()) {
            AuthorsAndBooksResponseDto authorsAndBooksResponseDto = convertToDto(genre.get());
            log.info("Genre: {}", authorsAndBooksResponseDto.getGenre());
            return authorsAndBooksResponseDto;
        } else {
            log.error("Genre with id: {} not found", id);
            throw new IllegalStateException("Жанр не найден");
        }
    }

    @Override
    public GenreCreateDto getGenreByName(String genreName) {
        Genre genre = genreRepository.findByName(genreName).orElseThrow();
        return convertGenreToDto(genre);
    }

    @Override
    public Genre getGenreByName2(String genreName) {
        return genreRepository.findByName(genreName).orElseThrow();
    }

    private GenreCreateDto convertGenreToDto(Genre genre) {
        return GenreCreateDto.builder()
                .name(genre.getName())
                .id(genre.getId())
                .build();
    }

    private AuthorsAndBooksResponseDto convertToDto(Genre genre) {
        List <Book> bookList = bookRepository.findByGenre(genre);
        Map<Author, List<Book>> authorToBooksMap = new HashMap<>();
        for (Book book : bookList) {
            for (Author author : book.getAuthors()) {
                List<Book> books = authorToBooksMap.get(author);
                if (books == null) {
                    books = new ArrayList<>();
                    authorToBooksMap.put(author, books);
                }
                books.add(book);
            }
        }

        List<AuthorDto> authorDtoList = new ArrayList<>();
        for (Map.Entry<Author, List<Book>> entry : authorToBooksMap.entrySet()) {
            Author author = entry.getKey();
            List<BookDto> authorBookDto = new ArrayList<>();
            for (Book book : entry.getValue()) {
                authorBookDto.add(new BookDto(book.getId(), book.getName(), null));
            }
            authorDtoList.add(new AuthorDto(author.getId(), author.getName(), author.getSurname(), authorBookDto));

        }
        return AuthorsAndBooksResponseDto.builder()
                .genre(genre.getName())
                .authors(authorDtoList)
                .build();
    }
}
