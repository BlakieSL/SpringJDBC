package org.example.springjdbc.repository.implementation;

import org.example.springjdbc.model.Book;
import org.example.springjdbc.repository.declaration.BookRepository;

import java.util.Optional;
import java.util.Set;

/**
    * This class implements the BookRepository interface using dedicated abstractions
    * to interact with the database and map the results
 */
public class BookRepositoryImpl implements BookRepository {
    @Override
    public Optional<Book> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Set<Book> findAll() {
        return Set.of();
    }

    @Override
    public Book create(Book book) {
        return null;
    }

    @Override
    public Book update(long id, Book book) {
        return null;
    }

    @Override
    public boolean delete(long id) {
        return false;
    }
}
