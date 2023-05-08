package ru.itgirl.libraryproject.service;

import ru.itgirl.libraryproject.dto.AuthorsAndBooksResponseDto;
import ru.itgirl.libraryproject.dto.GenreCreateDto;
import ru.itgirl.libraryproject.dto.GenreDto;
import ru.itgirl.libraryproject.model.Genre;

public interface GenreService {
    AuthorsAndBooksResponseDto getGenreById (Long id);

    GenreCreateDto getGenreByName(String genre);
    Genre getGenreByName2 (String genreName);
}
