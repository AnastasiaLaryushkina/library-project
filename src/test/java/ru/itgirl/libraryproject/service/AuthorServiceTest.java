package ru.itgirl.libraryproject.service;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import ru.itgirl.libraryproject.dto.AuthorCreateDto;
import ru.itgirl.libraryproject.dto.AuthorDto;
import ru.itgirl.libraryproject.model.Author;
import ru.itgirl.libraryproject.model.Book;
import ru.itgirl.libraryproject.repository.AuthorRepository;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AuthorServiceTest {

    @Mock
    AuthorRepository authorRepository;

    @InjectMocks
    AuthorServiceImpl authorService;

    @Test
    public void testGetAuthorById() {
        Long id = 1L;
        String name = "John";
        String surname = "Doe";
        Set<Book> books = new HashSet<>();

        Author author = new Author(id, name, surname, books);

        when(authorRepository.findById(id)).thenReturn(Optional.of(author));

        AuthorDto authorDto = authorService.getAuthorById(id);
        verify(authorRepository).findById(id);

        assertEquals(authorDto.getId(), author.getId());
        assertEquals(authorDto.getName(), author.getName());
        assertEquals(authorDto.getSurname(), author.getSurname());
    }

    @Test
    public void testGetAuthorByIdFailed() {
        Long id = 1L;

        when(authorRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalStateException.class, () -> authorService.getAuthorById(id));
        verify(authorRepository).findById(id);
    }
    @Test
    public void testGetAuthorByNameV1() {
        Long id = 1L;
        String name = "John";
        String surname = "Doe";
        Set<Book> books = new HashSet<>();

        Author author = new Author(id, name, surname, books);

        when(authorRepository.findAuthorByName(name)).thenReturn(Optional.of(author));

        AuthorDto authorDto = authorService.getByNameV1(name);
        verify(authorRepository).findAuthorByName(name);

        assertEquals(authorDto.getId(), author.getId());
        assertEquals(authorDto.getName(), author.getName());
        assertEquals(authorDto.getSurname(), author.getSurname());
    }

    @Test
    public void testGetAuthorByNameV1Failed() {
        String name = "John";

        when(authorRepository.findAuthorByName(name)).thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalStateException.class, () -> authorService.getByNameV1(name));
        verify(authorRepository).findAuthorByName(name);
    }

    @Test
    public void testGetAuthorByNameV2() {
        Long id = 1L;
        String name = "John";
        String surname = "Doe";
        Set<Book> books = new HashSet<>();

        Author author = new Author(id, name, surname, books);

        when(authorRepository.findAuthorByNameBySql(name)).thenReturn(Optional.of(author));

        AuthorDto authorDto = authorService.getByNameV2(name);
        verify(authorRepository).findAuthorByNameBySql(name);

        assertEquals(authorDto.getId(), author.getId());
        assertEquals(authorDto.getName(), author.getName());
        assertEquals(authorDto.getSurname(), author.getSurname());
    }

    @Test
    public void testGetAuthorByNameV2Failed() {
        String name = "John";

        when(authorRepository.findAuthorByNameBySql(name)).thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalStateException.class, () -> authorService.getByNameV2(name));
        verify(authorRepository).findAuthorByNameBySql(name);
    }

    @Test
    public void testGetAuthorByNameV3() {
        String name = "John";

        Author author = new Author();
        author.setName(name);

        when(authorRepository.findOne(any(Specification.class))).thenReturn(Optional.of(author));

        AuthorDto result = authorService.getByNameV3(name);
        assertNotNull(result);
        assertEquals(name,result.getName());
        verify(authorRepository).findOne(any(Specification.class));
    }

    @Test
    public void testGetAuthorByNameV3Failed() {
        String name = "John";
        Author author = new Author();
        author.setName(name);

        when(authorRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalStateException.class, () -> authorService.getByNameV3(name));
        verify(authorRepository).findOne(any(Specification.class));
    }

    @Test
    public void testCreateAuthor() {

        AuthorCreateDto authorCreateDto = new AuthorCreateDto();
        authorCreateDto.setName("John");
        authorCreateDto.setSurname("Doe");

        Author savedAuthor = new Author();
        savedAuthor.setName(authorCreateDto.getName());
        savedAuthor.setSurname(authorCreateDto.getSurname());

        when(authorRepository.save(Mockito.any(Author.class))).thenReturn(savedAuthor);

        AuthorDto result = authorService.createAuthor(authorCreateDto);

        assertNotNull(result);
        assertEquals(authorCreateDto.getName(), result.getName());
        assertEquals(authorCreateDto.getSurname(), result.getSurname());
        verify(authorRepository).save(Mockito.any(Author.class));
    }
}
