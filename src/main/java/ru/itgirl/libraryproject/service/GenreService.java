package ru.itgirl.libraryproject.service;

import ru.itgirl.libraryproject.dto.GenreDto;

public interface GenreService {
    GenreDto getGenreById (Long id);
}
