package ru.itgirl.libraryproject.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itgirl.libraryproject.dto.AuthorDto;
import ru.itgirl.libraryproject.dto.AuthorsAndBooksResponseDto;
import ru.itgirl.libraryproject.dto.BookDto;
import ru.itgirl.libraryproject.model.Author;
import ru.itgirl.libraryproject.model.Book;
import ru.itgirl.libraryproject.model.Genre;
import ru.itgirl.libraryproject.repository.BookRepository;
import ru.itgirl.libraryproject.repository.GenreRepository;

import java.util.*;




@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final BookRepository bookRepository;
    private final GenreRepository genreRepository;

    @Override
    public AuthorsAndBooksResponseDto getGenreById(Long id) {
        try {
            Genre genre = genreRepository.findById(id).orElseThrow(()-> new EntityNotFoundException());
            return convertToDto(genre);
        }  catch (EntityNotFoundException e) {
            return new AuthorsAndBooksResponseDto("Такой жанр отсутствует", Collections.EMPTY_LIST);
        }
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
