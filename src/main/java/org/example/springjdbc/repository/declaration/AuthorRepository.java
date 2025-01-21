package org.example.springjdbc.repository.declaration;

import org.example.springjdbc.model.Author;

import java.util.Optional;
import java.util.Set;

public interface AuthorRepository {
    Optional<Author> findByIdWithoutAssociations(long id);
    Optional<Author> findByIdWithAssociations(long id);
    Set<Author> findAll();
    Author create(Author author);
    Author update(long id, Author author);
    boolean delete(long id);
}
