package org.example.springjdbc.model;

import java.time.LocalDate;
import java.util.Set;

public record Book(
        Long id,
        Long authorId,
        String title,
        LocalDate releaseDate,
        Set<Library> libraries
) {}
