package ru.itgirl.libraryproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.itgirl.libraryproject.dto.AuthorCreateDto;
import ru.itgirl.libraryproject.dto.AuthorDto;
import ru.itgirl.libraryproject.dto.AuthorUpdateDto;
import ru.itgirl.libraryproject.model.Author;
import ru.itgirl.libraryproject.repository.AuthorRepository;
import ru.itgirl.libraryproject.service.AuthorService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class AuthorRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private ObjectMapper objectMapper;

    private Long createdAuthorId;

    @AfterEach
    public void cleanUp() {
        if (createdAuthorId != null) {
            authorRepository.deleteById(createdAuthorId);
        }
    }

    @Test
    public void testGetAuthorById() throws Exception{
        Long id = 1L;
        AuthorDto authorDto = AuthorDto.builder()
                .id(id)
                .name("Александр")
                .surname("Пушкин")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get("/author/{id}", id))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(authorDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(authorDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname").value(authorDto.getSurname()));
    }

    @Test
    public void testGetAuthorByNameV1() throws Exception {
        String name = "Александр";
        AuthorDto authorDto = AuthorDto.builder()
                .id(1L)
                .name(name)
                .surname("Пушкин")
                .build();

        //здесь использую perform() для выполнения GET-запроса на /author с заданным параметром name
        mockMvc.perform(MockMvcRequestBuilders.get("/author")
                //Создается GET-запрос на /author с заданным параметром name
                .param("name", name))
                //Ожидается, что статус ответа будет isOk() (200), то есть успешным
                .andExpect(status().isOk())
                //Ожидается, что возвращенный JSON-ответ будет содержать поле id, значение которого соответствует authorDto.getId()
                .andExpect(jsonPath("$.id").value(authorDto.getId()))
                //Ожидается, что возвращенный JSON-ответ будет содержать поле name, значение которого соответствует authorDto.getName()
                .andExpect(jsonPath("$.name").value(authorDto.getName()))
                //Ожидается, что возвращенный JSON-ответ будет содержать поле surname, значение которого соответствует authorDto.getSurname()
                .andExpect(jsonPath("$.surname").value(authorDto.getSurname()));
    }

    @Test
    public void testGetAuthorByNameV2() throws Exception {
        String name = "Александр";
        AuthorDto authorDto = AuthorDto.builder()
                .id(1L)
                .name(name)
                .surname("Пушкин")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get("/author/v2")
                        .param("name", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(authorDto.getId()))
                .andExpect(jsonPath("$.name").value(authorDto.getName()))
                .andExpect(jsonPath("$.surname").value(authorDto.getSurname()));
    }

    @Test
    public void testGetAuthorByNameV3() throws Exception {
        String name = "Александр";
        AuthorDto authorDto = AuthorDto.builder()
                .id(1L)
                .name(name)
                .surname("Пушкин")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get("/author/v3")
                        .param("name", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(authorDto.getId()))
                .andExpect(jsonPath("$.name").value(authorDto.getName()))
                .andExpect(jsonPath("$.surname").value(authorDto.getSurname()));
    }

    @Test
    public void testCreateAuthor() throws Exception {
        AuthorCreateDto authorCreateDto = new AuthorCreateDto();
        authorCreateDto.setName("Имя");
        authorCreateDto.setSurname("Фамилия");

        mockMvc.perform(MockMvcRequestBuilders.post("/author/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authorCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(authorCreateDto.getName()))
                .andExpect(jsonPath("$.surname").value(authorCreateDto.getSurname()))
                .andExpect(jsonPath("$.id").isNumber()) //Получение идентификатора созданного автора
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    createdAuthorId = objectMapper.readValue(response, AuthorDto.class).getId();
                });

        //Проверяем, что автор был создан
        List<Author> authors = authorRepository.findAll();
        assertEquals(7, authors.size());

        Author createdAuthor = authors.get(6);
        assertEquals(authorCreateDto.getName(), createdAuthor.getName());
        assertEquals(authorCreateDto.getSurname(), createdAuthor.getSurname());
    }

    @Test
    public void testUpdateAuthor() throws Exception {
        AuthorCreateDto authorCreateDto = new AuthorCreateDto();
        authorCreateDto.setName("Старое имя");
        authorCreateDto.setSurname("Старая фамилия");

        AuthorDto createdAuthor = authorService.createAuthor(authorCreateDto);

        AuthorUpdateDto authorUpdateDto = new AuthorUpdateDto();
        authorUpdateDto.setId(createdAuthor.getId());
        authorUpdateDto.setName("Новое имя");
        authorUpdateDto.setSurname("Новая Фамилия");

        mockMvc.perform(MockMvcRequestBuilders.put("/author/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authorUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(authorUpdateDto.getName()))
                .andExpect(jsonPath("$.surname").value(authorUpdateDto.getSurname()))
                .andExpect(jsonPath("$.id").isNumber()) //Получение идентификатора созданного автора
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    createdAuthorId = objectMapper.readValue(response, AuthorDto.class).getId();
                });

        Author updatedAuthor = authorRepository.findById(createdAuthor.getId()).orElse(null);
        assertNotNull(updatedAuthor);
        assertEquals(authorUpdateDto.getName(), updatedAuthor.getName());
        assertEquals(authorUpdateDto.getSurname(), updatedAuthor.getSurname());
    }

    @Test
    public void testDeleteAuthor() throws Exception {
        AuthorCreateDto authorCreateDto = new AuthorCreateDto();
        authorCreateDto.setName("Имя");
        authorCreateDto.setSurname("Фамилия");

        AuthorDto createdAuthor = authorService.createAuthor(authorCreateDto);

        mockMvc.perform(MockMvcRequestBuilders.delete("/author/delete/{id}", createdAuthor.getId()))
                .andExpect(status().isOk());

        Optional<Author> deletedAuthor = authorRepository.findById(createdAuthor.getId());
        assertFalse(deletedAuthor.isPresent());
    }
}
