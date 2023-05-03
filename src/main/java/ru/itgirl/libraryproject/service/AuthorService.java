package ru.itgirl.libraryproject.service;

import ru.itgirl.libraryproject.dto.AuthorDto;

public interface AuthorService {
    AuthorDto getAuthorById(Long id);
    AuthorDto getByNameV1(String name);
    AuthorDto getByNameV2(String name);
    AuthorDto getByNameV3(String name);

}
