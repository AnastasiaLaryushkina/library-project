package ru.itgirl.libraryproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GenreCreateDto {
    @Size(min = 3, max = 10)
    @NotBlank(message = "Необходимо добавить название жанра")
    private String name;
    private Long id;
}
