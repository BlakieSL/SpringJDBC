package org.example.springjdbc;

import org.example.springjdbc.model.Author;
import org.example.springjdbc.repository.declaration.AuthorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests the AuthorRepository class using same database as specified in
 * production properties and static SQL scripts.
 */

@SpringBootTest
public class AuthorRepositoryTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorRepositoryTest.class);

    private AuthorRepository authorRepository;

    @Autowired
    public void setAuthorRepository(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @SqlSetupAuthorBook
    @DisplayName("TEST findByIdWithoutAssociations - Should return Author")
    @Test
    void testFindByIdWithoutAssociations() {
        var result = authorRepository.findByIdWithoutAssociations(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().id());
        assertEquals("John", result.get().firstName());
        assertEquals("Doe", result.get().lastName());
        assertTrue(result.get().books().isEmpty());
    }

    @SqlSetupAuthorBook
    @DisplayName("TEST findByIdWithoutAssociations - Should return Optional.empty()")
    @Test
    void testFindByIdWithoutAssociationsNotFound() {
        var result = authorRepository.findByIdWithoutAssociations(999L);

        assertTrue(result.isEmpty());
    }

    @SqlSetupAuthorBook
    @DisplayName("TEST findByIdWithAssociations - Should return Author")
    @Test
    void testFindByIdWithAssociations() {
        var result = authorRepository.findByIdWithAssociations(1L);
        assertTrue(result.isPresent());

        var author = result.get();
        assertEquals(1L, author.id());
        assertEquals("John", author.firstName());
        assertEquals("Doe", author.lastName());
        assertEquals(2, author.books().size());
        assertTrue(author.books().stream().anyMatch(book -> book.id() == 1L && "Book One by John".equals(book.title())));
        assertTrue(author.books().stream().anyMatch(book -> book.id() == 2L && "Book Two by John".equals(book.title())));
    }

    @SqlSetupAuthorBook
    @DisplayName("TEST findByIdWithAssociations - Should return Optional.empty()")
    @Test
    void testFindByIdWithAssociationsNotFound() {
        var result = authorRepository.findByIdWithAssociations(999L);
        assertTrue(result.isEmpty());
    }

    @SqlSetupAuthorBook
    @DisplayName("TEST findAll - Should return all Authors")
    @Test
    void testFindAll() {
        var result = authorRepository.findAll();
        assertFalse(result.isEmpty());
        assertEquals(3, result.size());

        assertTrue(result.stream().anyMatch(author ->
                author.id() == 1L &&
                        "John".equals(author.firstName()) &&
                        "Doe".equals(author.lastName()) &&
                        author.books().size() == 2 &&
                        author.books().stream().anyMatch(book -> book.id() == 1L && "Book One by John".equals(book.title())) &&
                        author.books().stream().anyMatch(book -> book.id() == 2L && "Book Two by John".equals(book.title()))
        ));

        assertTrue(result.stream().anyMatch(author ->
                author.id() == 2L &&
                        "Jane".equals(author.firstName()) &&
                        "Smith".equals(author.lastName()) &&
                        author.books().size() == 1 &&
                        author.books().stream().anyMatch(book -> book.id() == 3L && "Jane's Journey".equals(book.title()))
        ));

        assertTrue(result.stream().anyMatch(author ->
                author.id() == 3L &&
                        "Emily".equals(author.firstName()) &&
                        "Johnson".equals(author.lastName()) &&
                        author.books().size() == 1 &&
                        author.books().stream().anyMatch(book -> book.id() == 4L && "Emily's Adventures".equals(book.title()))
        ));
    }

    @DisplayName("TEST findAll - Should return empty Set")
    @Test
    void testFindAllEmpty() {
        var result = authorRepository.findAll();
        assertTrue(result.isEmpty());
    }

    @SqlSetupAuthorBook
    @DisplayName("TEST create - Should create and return Author")
    @Test
    void testCreate() {
        var newAuthor = new Author(null, "Alice", "Walker", Set.of());
        var createdAuthor = authorRepository.create(newAuthor);

        assertNotNull(createdAuthor);
        assertNotNull(createdAuthor.id());
        assertEquals("Alice", createdAuthor.firstName());
        assertEquals("Walker", createdAuthor.lastName());
        assertTrue(createdAuthor.books().isEmpty());
    }

    @DisplayName("TEST create - Should fail for missing fields")
    @Test
    void testCreateWithMissingFields() {
        var incompleteAuthor = new Author(null, null, "Walker", Set.of());

        assertThrows(Exception.class, () -> authorRepository.create(incompleteAuthor));
    }

    @SqlSetupAuthorBook
    @DisplayName("TEST update - Should update and return Author")
    @Test
    void testUpdate() {
        var existingAuthor = new Author(1L, "UpdatedFirstName", "UpdatedLastName", Set.of());
        var updatedAuthor = authorRepository.update(1L, existingAuthor);

        assertNotNull(updatedAuthor);
        assertEquals(1L, updatedAuthor.id());
        assertEquals("UpdatedFirstName", updatedAuthor.firstName());
        assertEquals("UpdatedLastName", updatedAuthor.lastName());
        assertTrue(updatedAuthor.books().isEmpty());
    }

    @SqlSetupAuthorBook
    @DisplayName("TEST update - Should throw exception when Author not found")
    @Test
    void testUpdateNotFound() {
        var nonExistentAuthor = new Author(999L, "NonExistent", "Author", Set.of());

        var exception = assertThrows(
                RuntimeException.class,
                () -> authorRepository.update(999L, nonExistentAuthor)
        );

        assertEquals("Failed to update author", exception.getMessage());
    }

    @SqlSetupAuthorBook
    @DisplayName("TEST delete - Should delete and return true")
    @Test
    void testDelete() {
        boolean isDeleted = authorRepository.delete(1L);

        assertTrue(isDeleted);
        Optional<Author> deletedAuthor = authorRepository.findByIdWithoutAssociations(1L);
        assertTrue(deletedAuthor.isEmpty());
    }

    @SqlSetupAuthorBook
    @DisplayName("TEST delete - Should return false when Author not found")
    @Test
    void testDeleteNotFound() {
        boolean isDeleted = authorRepository.delete(999L);

        assertFalse(isDeleted);
    }
}
