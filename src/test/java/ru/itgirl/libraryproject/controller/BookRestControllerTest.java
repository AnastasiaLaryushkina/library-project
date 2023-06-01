package ru.itgirl.libraryproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.itgirl.libraryproject.dto.BookCreateDto;
import ru.itgirl.libraryproject.dto.BookDto;
import ru.itgirl.libraryproject.dto.BookUpdateDto;
import ru.itgirl.libraryproject.model.Book;
import ru.itgirl.libraryproject.model.Genre;
import ru.itgirl.libraryproject.repository.BookRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class BookRestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private Book setUpBook;

    @BeforeEach
    public void setUp() {
        Genre genre = new Genre(1L, "Рассказ");
        setUpBook = new Book();
        setUpBook.setName("Название книги");
        setUpBook.setGenre(genre);
        setUpBook = bookRepository.save(setUpBook);
    }

    @AfterEach
    public void cleanUp() {
        bookRepository.delete(setUpBook);
    }

    @Test
    public void getBookByNameV1() throws Exception {
        String name = "Название книги";
        mockMvc.perform(MockMvcRequestBuilders.get("/book")
                .param("name", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    public void getBookByNameV2() throws Exception {
        String name = "Название книги";
        mockMvc.perform(MockMvcRequestBuilders.get("/book/v2")
                        .param("name", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.id").value(setUpBook.getId()));
    }

    @Test
    public void getBookByNameV3() throws Exception {
        String name = "Название книги";
        mockMvc.perform(MockMvcRequestBuilders.get("/book/v3")
                        .param("name", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.id").value(setUpBook.getId()));
    }

    @Test
    void createBook() throws Exception {
        BookCreateDto bookCreateDto = new BookCreateDto();
        bookCreateDto.setName("Название книги");
        bookCreateDto.setGenre("Рассказ");

        mockMvc.perform(MockMvcRequestBuilders.post("/book/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(bookCreateDto.getName()))
                .andExpect(jsonPath("$.genre").value(bookCreateDto.getGenre()))
                .andExpect(jsonPath("$.id").isNumber())
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    Long createdBookId = objectMapper.readValue(response, BookDto.class).getId();
                    bookRepository.deleteById(createdBookId);
                });
    }

    @Test
    void updateBook() throws Exception {
        Genre genre = new Genre(1L, "Рассказ");
        Book testBook = new Book();
        testBook.setName("Старое название книги");
        testBook.setGenre(genre);
        testBook = bookRepository.save(testBook);
        BookUpdateDto bookUpdateDto = new BookUpdateDto();
        bookUpdateDto.setId(testBook.getId());
        bookUpdateDto.setName("Новое название книги");
        bookUpdateDto.setGenre("Роман");

        mockMvc.perform(MockMvcRequestBuilders.put("/book/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(bookUpdateDto.getName()));

        Book updatedBook = bookRepository.findById(testBook.getId()).orElse(null);
        assertNotNull(updatedBook);
        assertEquals(bookUpdateDto.getName(), updatedBook.getName());
        bookRepository.delete(testBook);

    }

    @Test
    void deleteBook() throws Exception {
        Genre genre = new Genre(1L, "Рассказ");
        Book book = new Book();
        book.setName("Удаляемая книга");
        book.setGenre(genre);
        book = bookRepository.save(book);

        mockMvc.perform(MockMvcRequestBuilders.delete("/book/delete/{id}", book.getId()))
                .andExpect(status().isOk());

        assertFalse(bookRepository.existsById(book.getId()));
    }
}
