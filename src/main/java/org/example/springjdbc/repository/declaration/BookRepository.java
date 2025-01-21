package org.example.springjdbc.repository.declaration;

import org.example.springjdbc.model.Book;

import java.util.Optional;
import java.util.Set;

public interface BookRepository {
    Optional<Book> findById(Long id);
    Set<Book> findAll();
    Book create(Book book);
    Book update(long id, Book book);
    boolean delete(long id);
}
