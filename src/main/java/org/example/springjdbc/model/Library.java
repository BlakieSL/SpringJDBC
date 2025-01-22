package org.example.springjdbc.model;

import java.util.Set;

public record Library(
        Long id,
        String name,
        LibraryInfo libraryInfo,
        Set<Book> books
) {}
