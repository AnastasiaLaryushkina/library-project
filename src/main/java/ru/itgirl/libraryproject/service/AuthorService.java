package ru.itgirl.libraryproject.service;

import ru.itgirl.libraryproject.dto.AuthorCreateDto;
import ru.itgirl.libraryproject.dto.AuthorDto;
import ru.itgirl.libraryproject.dto.AuthorUpdateDto;

public interface AuthorService {
    AuthorDto getAuthorById(Long id);
    AuthorDto getByNameV1(String name);
    AuthorDto getByNameV2(String name);
    AuthorDto getByNameV3(String name);

    AuthorDto createAuthor(AuthorCreateDto authorCreateDto);
    AuthorDto updateAuthor(AuthorUpdateDto authorUpdateDto);

    void deleteAuthor(Long id);

}
