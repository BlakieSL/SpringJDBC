package org.example.springjdbc.model;

import java.util.Set;

public record Author(
        Long id,
        String firstName,
        String lastName,
        Set<Book> books
) {}
