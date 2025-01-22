package org.example.springjdbc;

import org.example.springjdbc.model.Book;
import org.example.springjdbc.repository.declaration.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests the BookRepository class using testcontainers
 */

@ActiveProfiles("test")
@Sql(
        scripts = {
                "classpath:/schema/drop-schema.sql",
                "classpath:/schema/create-schema.sql"
        }
)
@Testcontainers
@SpringBootTest
public class BookRepositoryTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookRepositoryTest.class);
    private BookRepository bookRepository;

    @Autowired
    public void setBookRepository(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @SqlSetupAuthorBook
    @DisplayName("TEST findByIdWithAssociations - Should return Book with Libraries")
    @Test
    void testFindByIdWithAssociations() {
        var result = bookRepository.findByIdWithAssociations(1L);

        assertTrue(result.isPresent());

        var book = result.get();
        assertEquals(1L, book.id());
        assertEquals(1L, book.authorId());
        assertEquals("Book One by John", book.title());
        assertEquals(LocalDate.of(2023, 1, 15), book.releaseDate());

        var libraries = book.libraries();
        assertNotNull(libraries);
        assertEquals(1, libraries.size());

        var library = libraries.iterator().next();
        assertEquals(1L, library.id());
        assertEquals("Central Library", library.name());
        assertNull(library.libraryInfo());
    }

    @SqlSetupAuthorBook
    @DisplayName("TEST findByIdWithAssociations - Should return Optional.empty()")
    @Test
    void testFindByIdNotFound() {
        var result = bookRepository.findByIdWithAssociations(999L);

        assertTrue(result.isEmpty());
    }

    @SqlSetupAuthorBook
    @DisplayName("TEST findAll - Should return all Books")
    @Test
    void testFindAll() {
        var result = bookRepository.findAll();
        assertFalse(result.isEmpty());
        assertEquals(4, result.size());

        assertTrue(result.stream().anyMatch(book ->
                book.id() == 1L &&
                        book.authorId() == 1L &&
                        "Book One by John".equals(book.title()) &&
                        LocalDate.of(2023, 1, 15).equals(book.releaseDate())
        ));

        assertTrue(result.stream().anyMatch(book ->
                book.id() == 2L &&
                        book.authorId() == 1L &&
                        "Book Two by John".equals(book.title()) &&
                        LocalDate.of(2023, 3, 10).equals(book.releaseDate())
        ));

        assertTrue(result.stream().anyMatch(book ->
                book.id() == 3L &&
                        book.authorId() == 2L &&
                        "Jane's Journey".equals(book.title()) &&
                        LocalDate.of(2022, 5, 22).equals(book.releaseDate())
        ));

        assertTrue(result.stream().anyMatch(book ->
                book.id() == 4L &&
                        book.authorId() == 3L &&
                        "Emily's Adventures".equals(book.title()) &&
                        LocalDate.of(2021, 12, 5).equals(book.releaseDate())
        ));
    }

    @DisplayName("TEST findAll - Should return empty Set")
    @Test
    void testFindAllEmpty() {
        var result = bookRepository.findAll();
        assertTrue(result.isEmpty());
    }

    @SqlSetupAuthorBook
    @DisplayName("TEST create - Should create and return Book")
    @Test
    void testCreate() {
        var newBook = new Book(null, 1L, "New Book", LocalDate.of(2023, 10, 10), Set.of());
        var createdBook = bookRepository.create(newBook);

        assertNotNull(createdBook);
        assertNotNull(createdBook.id());
        assertEquals(1L, createdBook.authorId());
        assertEquals("New Book", createdBook.title());
        assertEquals(LocalDate.of(2023, 10, 10), createdBook.releaseDate());
    }

    @DisplayName("TEST create - Should fail for missing fields")
    @Test
    void testCreateWithMissingFields() {
        var incompleteBook = new Book(null, null, "Incomplete Book", null, Set.of());

        assertThrows(Exception.class, () -> bookRepository.create(incompleteBook));
    }

    @SqlSetupAuthorBook
    @DisplayName("TEST update - Should update and return Book")
    @Test
    void testUpdate() {
        var existingBook = new Book(1L, 1L, "Updated Title", LocalDate.of(2023, 1, 15), Set.of());
        var updatedBook = bookRepository.update(1L, existingBook);

        assertNotNull(updatedBook);
        assertEquals(1L, updatedBook.id());
        assertEquals(1L, updatedBook.authorId());
        assertEquals("Updated Title", updatedBook.title());
        assertEquals(LocalDate.of(2023, 1, 15), updatedBook.releaseDate());
    }

    @SqlSetupAuthorBook
    @DisplayName("TEST update - Should throw exception when Book not found")
    @Test
    void testUpdateNotFound() {
        var nonExistentBook = new Book(999L, 1L, "NonExistent Book", LocalDate.of(2023, 1, 15), Set.of());

        var exception = assertThrows(
                RuntimeException.class,
                () -> bookRepository.update(999L, nonExistentBook)
        );

        assertEquals("Failed to update book", exception.getMessage());
    }

    @SqlSetupAuthorBook
    @DisplayName("TEST delete - Should delete and return true")
    @Test
    void testDelete() {
        boolean isDeleted = bookRepository.delete(1L);

        assertTrue(isDeleted);
        Optional<Book> deletedBook = bookRepository.findByIdWithAssociations(1L);
        assertTrue(deletedBook.isEmpty());
    }

    @SqlSetupAuthorBook
    @DisplayName("TEST delete - Should return false when Book not found")
    @Test
    void testDeleteNotFound() {
        boolean isDeleted = bookRepository.delete(999L);

        assertFalse(isDeleted);
    }
}
