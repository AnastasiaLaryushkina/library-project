package ru.itgirl.libraryproject.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.itgirl.libraryproject.dto.*;
import ru.itgirl.libraryproject.model.Author;
import ru.itgirl.libraryproject.model.Book;
import ru.itgirl.libraryproject.model.Genre;
import ru.itgirl.libraryproject.repository.BookRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final GenreService genreService;
    @Override
    public BookDto getByNameV1(String name) {
        log.info("Try to find book by name {}", name);
        Optional<Book> book = bookRepository.findBookByName(name);
        if (book.isPresent()) {
            BookDto bookDto = convertEntityToDto(book.get());
            log.info("Book: {}", bookDto.toString());
            return bookDto;
        } else {
            log.error("Book with name: {} not found", name);
            throw new IllegalStateException("Книга не найдена");
        }
    }

    @Override
    public BookDto getByNameV2(String name) {
        log.info("Try to find book by name {}", name);
        Optional<Book> book = bookRepository.findBookByNameBySql(name);
        if (book.isPresent()) {
            BookDto bookDto = convertEntityToDto(book.get());
            log.info("Book: {}", bookDto.toString());
            return bookDto;
        } else {
            log.error("Book with name: {} not found", name);
            throw new IllegalStateException("Книга не найдена");
        }
    }

    @Override
    public BookDto getByNameV3(String name) {
        log.info("Try to find book by name {}", name);
        Specification<Book> bookSpecification = Specification.where(new Specification<Book>() {
            @Override
            public Predicate toPredicate(Root<Book> root,
                                         CriteriaQuery<?> query,
                                         CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("name"), name);
            }
        });
        Optional<Book> book = bookRepository.findOne(bookSpecification);
        if (book.isPresent()) {
            BookDto bookDto = convertEntityToDto(book.get());
            log.info("Book: {}", bookDto.toString());
            return bookDto;
        } else {
            log.error("Book with name: {} not found", name);
            throw new IllegalStateException("Книга не найдена");
        }
    }

    @Override
    public BookDto createBook(BookCreateDto bookCreateDto) {
        log.info("Try to create book {} {}", bookCreateDto.getName(), bookCreateDto.getGenre());
        GenreCreateDto genreCreateDto = genreService.getGenreByName(bookCreateDto.getGenre());
        Book book = bookRepository.save(convertDtoToEntity(bookCreateDto, genreCreateDto));
        BookDto bookDto = convertEntityToDto(book);
        log.info("Book is created {} {}", book.getName(), book.getGenre());
        return bookDto;
    }

    @Override
    public BookDto updateBook(BookUpdateDto bookUpdateDto) {

        log.info("Try to update book by id {}", bookUpdateDto.getId());
        Optional<Book> book = bookRepository.findById(bookUpdateDto.getId());
        Genre genre = genreService.getGenreByName2(bookUpdateDto.getGenre());
        if (book.isPresent()) {
            Book bookObj = book.get();
            bookObj.setName(bookUpdateDto.getName());
            bookObj.setGenre(genre);
            Book savedBook = bookRepository.save(bookObj);
            BookDto bookDto = convertEntityToDto(savedBook);
            log.info("Book was updated {} {}", bookDto.getName(), bookDto.getGenre());
            return bookDto;
        } else {
            log.error("Book with id: {} not found", bookUpdateDto.getId());
            throw new IllegalStateException("Книга не найдена");
        }
    }

    @Override
    public void deleteBook(Long id) {
        log.info("Try to delete book by id {}", id);
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            bookRepository.deleteById(id);
            log.info("Book was deleted, id = {}", id);
        } else {
            log.error("Book with id: {} not found", id);
            throw new IllegalStateException("Книга не найден");
        }
    }

    @Override
    public List<BookDto> getAllBooks() {
        log.info("Try to get all books");
        List<Book> books = bookRepository.findAll();
        log.info("All books got");
        return books.stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

    private Book convertDtoToEntity(BookCreateDto bookCreateDto,GenreCreateDto genreCreateDto) {
        Genre genre = Genre.builder()
                .name(genreCreateDto.getName())
                .id(genreCreateDto.getId())
                .build();

        return Book.builder()
                .name(bookCreateDto.getName())
                .genre(genre)
                .build();
    }

    private BookDto convertEntityToDto(Book book) {
        return BookDto.builder()
                .name(book.getName())
                .genre(book.getGenre().getName())
                .id(book.getId())
                .build();
    }
}
