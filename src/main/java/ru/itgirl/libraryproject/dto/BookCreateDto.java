package ru.itgirl.libraryproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itgirl.libraryproject.model.Genre;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BookCreateDto {
    @Size(min = 1, max = 100)
    @NotBlank(message = "Необходимо добавить название")
    private String name;
    @NotBlank(message = "Необходимо добавить жанр")
    private String genre;
}
