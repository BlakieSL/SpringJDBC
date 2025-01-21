package org.example.springjdbc.model;

import java.time.LocalDate;

public record Book(
        Long id,
        Long authorId,
        String title,
        LocalDate releaseDate
) {}
