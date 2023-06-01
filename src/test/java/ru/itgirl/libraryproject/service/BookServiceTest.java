package ru.itgirl.libraryproject.service;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import ru.itgirl.libraryproject.dto.*;
import ru.itgirl.libraryproject.model.Book;
import ru.itgirl.libraryproject.model.Genre;
import ru.itgirl.libraryproject.repository.BookRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BookServiceTest {

    @Mock
    BookRepository bookRepository;

    @InjectMocks
    BookServiceImpl bookService;

    @Mock
    GenreServiceImpl genreService;

    @Test
    public void testGetBookByNameV1() {
        String name = "Медный всадник";
        Genre genre = new Genre(1L,"Роман");
        Book book = new Book();
        book.setName(name);
        book.setGenre(genre);

        when(bookRepository.findBookByName(name)).thenReturn(Optional.of(book));

        BookDto bookDto = bookService.getByNameV1(name);
        verify(bookRepository).findBookByName(name);

        assertEquals(bookDto.getName(), book.getName());
        assertEquals(bookDto.getGenre(), book.getGenre().getName());
    }

    @Test
    public void testGetBookByNameV1Failed() {
        String name = "Медный всадник";

        when(bookRepository.findBookByName(name)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> bookService.getByNameV1(name));
        verify(bookRepository).findBookByName(name);
    }

    @Test
    public void testGetBookByNameV2() {
        String name = "Медный всадник";
        Genre genre = new Genre(1L,"Роман");
        Book book = new Book();
        book.setName(name);
        book.setGenre(genre);

        when(bookRepository.findBookByNameBySql(name)).thenReturn(Optional.of(book));

        BookDto bookDto = bookService.getByNameV2(name);
        verify(bookRepository).findBookByNameBySql(name);

        assertEquals(bookDto.getId(), book.getId());
        assertEquals(bookDto.getName(), book.getName());
        assertEquals(bookDto.getGenre(), book.getGenre().getName());
    }

    @Test
    public void testGetBookByNameV2Failed() {
        String name = "Медный всадник";

        when(bookRepository.findBookByNameBySql(name)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> bookService.getByNameV2(name));
        verify(bookRepository).findBookByNameBySql(name);
    }

    @Test
    public void testGetBookByNameV3() {
        String name = "Медный всадник";
        Genre genre = new Genre(1L,"Роман");
        Book book = new Book();
        book.setName(name);
        book.setGenre(genre);

        when(bookRepository.findOne(any(Specification.class))).thenReturn(Optional.of(book));

        BookDto result = bookService.getByNameV3(name);
        assertNotNull(result);
        assertEquals(name,result.getName());
        verify(bookRepository).findOne(any(Specification.class));
    }

    @Test
    public void testGetBookByNameV3Failed() {
        String name = "Медный всадник";
        Genre genre = new Genre(1L,"Роман");
        Book book = new Book();
        book.setName(name);
        book.setGenre(genre);

        when(bookRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> bookService.getByNameV3(name));
        verify(bookRepository).findOne(any(Specification.class));
    }

    @Test
    public void testCreateBook() {

        BookCreateDto bookCreateDto = new BookCreateDto();
        bookCreateDto.setName("Медный всадник");
        bookCreateDto.setGenre("Роман");

        GenreCreateDto genreCreateDto = new GenreCreateDto();
        genreCreateDto.setName("Роман");
        genreCreateDto.setId(1L);
        when(genreService.getGenreByName("Роман")).thenReturn(genreCreateDto);

        Book book = new Book();
        book.setName("Медный всадник");
        book.setGenre(new Genre(1L, "Роман"));
        when(bookRepository.save(Mockito.any(Book.class))).thenReturn(book);

        BookDto bookDto = bookService.createBook(bookCreateDto);

        assertNotNull(bookDto);
        assertEquals("Медный всадник", bookDto.getName());
        assertEquals("Роман", bookDto.getGenre());

        verify(genreService).getGenreByName("Роман");
        verify(bookRepository).save(Mockito.any(Book.class));
    }

    @Test
    public void testUpdateBook() {
        Long bookId = 1L;
        String updatedName = "Updated Book Name";
        String updatedGenre = "Updated Genre";
        BookUpdateDto bookUpdateDto = new BookUpdateDto();
        bookUpdateDto.setId(bookId);
        bookUpdateDto.setName(updatedName);
        bookUpdateDto.setGenre(updatedGenre);

        Book existingBook = new Book();
        existingBook.setName("Old Book Name");
        existingBook.setGenre(new Genre (1L, "Old Genre"));

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(genreService.getGenreByName2(updatedGenre)).thenReturn(new Genre (2L, updatedGenre));
        when(bookRepository.save(any(Book.class))).then(AdditionalAnswers.returnsFirstArg());

        BookDto result = bookService.updateBook(bookUpdateDto);

        assertNotNull(result);
        assertEquals(updatedName, result.getName());
        assertEquals(updatedGenre, result.getGenre());

        verify(bookRepository).findById(bookId);
        verify(genreService).getGenreByName2(updatedGenre);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    public void testUpdateBookFailed() {
        Long bookId = 1L;
        BookUpdateDto bookUpdateDto = new BookUpdateDto();
        bookUpdateDto.setId(bookId);
        bookUpdateDto.setName("Updated Book Name");
        bookUpdateDto.setGenre("Updated Genre");

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> bookService.updateBook(bookUpdateDto));

        verify(bookRepository).findById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    public void testDeleteBook() {
        Long bookId = 1L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(new Book()));

        bookService.deleteBook(bookId);

        verify(bookRepository).findById(bookId);
        verify(bookRepository).deleteById(bookId);
    }

    @Test
    public void testDeleteBookFailed() {
        Long bookId = 1L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> bookService.deleteBook(bookId));

        verify(bookRepository).findById(bookId);
    }

    @Test
    public void testGetAllBooks() {
        List<Book> books = new ArrayList<>();
        Book book1 = new Book();
        Book book2 = new Book();
        book1.setName("Book 1");
        book1.setGenre(new Genre(1L, "Роман"));
        book2.setName("Book 2");
        book2.setGenre(new Genre(2L, "Рассказ"));
        books.add(book1);
        books.add(book2);

        when(bookRepository.findAll()).thenReturn(books);

        List<BookDto> result = bookService.getAllBooks();

        assertNotNull(result);
        assertEquals(books.size(), result.size());

        verify(bookRepository).findAll();

        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            BookDto bookDto = result.get(i);
            assertEquals(book.getName(), bookDto.getName());
            assertEquals(book.getGenre().getName(), bookDto.getGenre());
        }
    }
}
