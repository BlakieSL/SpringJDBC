package org.example.springjdbc.model;

public record Library(
        Long id,
        String name,
        LibraryInfo libraryInfo
) {}
