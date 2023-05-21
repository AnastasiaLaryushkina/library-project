package ru.itgirl.libraryproject.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.itgirl.libraryproject.dto.AuthorCreateDto;
import ru.itgirl.libraryproject.dto.AuthorDto;
import ru.itgirl.libraryproject.dto.AuthorUpdateDto;
import ru.itgirl.libraryproject.dto.BookDto;
import ru.itgirl.libraryproject.model.Author;
import ru.itgirl.libraryproject.model.Book;
import ru.itgirl.libraryproject.repository.AuthorRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Override
    public AuthorDto getAuthorById(Long id) {
        log.info("Try to find author by id {}", id);
        Optional<Author> author = authorRepository.findById(id);
        if (author.isPresent()) {
            AuthorDto authorDto = convertEntityToDto(author.get());
            log.info("Author: {}", authorDto.toString());
            return authorDto;
        } else {
            log.error("Author with id: {} not found", id);
            throw new IllegalStateException("Автор не найден");
        }
    }

    @Override
    public AuthorDto getByNameV1(String name) {
        log.info("Try to find author by name {}", name);
        Optional<Author> author = authorRepository.findAuthorByName(name);
        if (author.isPresent()) {
            AuthorDto authorDto = convertEntityToDto(author.get());
            log.info("Author: {}", authorDto.toString());
            return authorDto;
        } else {
            log.error("Author with name: {} not found", name);
            throw new IllegalStateException("Автор не найден");
        }
    }

    @Override
    public AuthorDto getByNameV2(String name) {
        log.info("Try to find author by name {}", name);
        Optional<Author> author = authorRepository.findAuthorByName(name);
        if (author.isPresent()) {
            AuthorDto authorDto = convertEntityToDto(author.get());
            log.info("Author: {}", authorDto.toString());
            return authorDto;
        } else {
            log.error("Author with name: {} not found", name);
            throw new IllegalStateException("Автор не найден");
        }
    }

    @Override
    public AuthorDto getByNameV3(String name) {
        log.info("Try to find author by name {}", name);
        Specification<Author> authorSpecification = Specification.where(new Specification<Author>() {
            @Override
            public Predicate toPredicate(Root<Author> root,
                                         CriteriaQuery<?> query,
                                         CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("name"), name);
            }
        });
        Optional<Author> author = authorRepository.findOne(authorSpecification);
        if (author.isPresent()) {
            AuthorDto authorDto = convertEntityToDto(author.get());
            log.info("Author: {}", authorDto.toString());
            return authorDto;
        } else {
            log.error("Author with name: {} not found", name);
            throw new IllegalStateException("Автор не найден");
        }
    }

    @Override
    public AuthorDto createAuthor(AuthorCreateDto authorCreateDto) {
        log.info("Try to create author {} {}", authorCreateDto.getName(), authorCreateDto.getSurname());
        Author author = authorRepository.save(convertDtoToEntity(authorCreateDto));
        AuthorDto authorDto = convertEntityToDto(author);
        log.info("Author is created {} {}", author.getName(), author.getSurname());
        return authorDto;
    }

    @Override
    public AuthorDto updateAuthor(AuthorUpdateDto authorUpdateDto) {
        log.info("Try to update author by id {}", authorUpdateDto.getId());
        Optional<Author> author = authorRepository.findById(authorUpdateDto.getId());
        if (author.isPresent()) {
            Author authorObj = author.get();
            authorObj.setName(authorUpdateDto.getName());
            authorObj.setSurname(authorUpdateDto.getSurname());
            Author savedAuthor = authorRepository.save(authorObj);
            AuthorDto authorDto = convertEntityToDto(savedAuthor);
            log.info("Author was updated {} {}", authorDto.getName(), authorDto.getSurname());
            return authorDto;
        } else {
            log.error("Author with id: {} not found", authorUpdateDto.getId());
            throw new IllegalStateException("Автор не найден");
        }
    }

    @Override
    public void deleteAuthor(Long id) {
        log.info("Try to delete author by id {}", id);
        Optional<Author> author = authorRepository.findById(id);
        if (author.isPresent()) {
            authorRepository.deleteById(id);
            log.info("Author was deleted, id = {}", id);
        } else {
            log.error("Author with id: {} not found", id);
            throw new IllegalStateException("Автор не найден");
        }
    }

    @Override
    public List<AuthorDto> getAllAuthors() {
        log.info("Try to get all authors");
        List<Author> authors = authorRepository.findAll();
        log.info("All authors got");
        return authors.stream().map(this::convertEntityToDto).collect(Collectors.toList());
    }

    private Author convertDtoToEntity(AuthorCreateDto authorCreateDto) {
        return Author.builder()
                .name(authorCreateDto.getName())
                .surname(authorCreateDto.getSurname())
                .build();
    }

    private AuthorDto convertEntityToDto(Author author){
        List<BookDto> bookDtoList = null;
        if (author.getBooks() != null){
            bookDtoList = author.getBooks()
                    .stream()
                    .map(book -> BookDto.builder()
                            .genre(book.getGenre().getName())
                            .name(book.getName())
                            .id(book.getId())
                            .build()
                    ).toList();
        }

        AuthorDto authorDto = AuthorDto.builder()
                .id(author.getId())
                .name(author.getName())
                .surname(author.getSurname())
                .books(bookDtoList)
                .build();
        return authorDto;

    }
}
