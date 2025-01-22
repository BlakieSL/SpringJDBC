package org.example.springjdbc;

import org.example.springjdbc.model.Book;
import org.example.springjdbc.model.Library;
import org.example.springjdbc.model.LibraryInfo;
import org.example.springjdbc.repository.declaration.LibraryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the LibraryRepository class with real SQL data loading.
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
public class LibraryRepositoryTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryRepositoryTest.class);

    private final LibraryRepository libraryRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LibraryRepositoryTest(LibraryRepository libraryRepository, JdbcTemplate jdbcTemplate) {
        this.libraryRepository = libraryRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    void setUp() {
        resetDatabase();
        seedDatabase();
    }

    @AfterEach
    void tearDown() {
        resetDatabase();
    }

    private void resetDatabase() {
        jdbcTemplate.update("DELETE FROM library_book");
        jdbcTemplate.update("DELETE FROM library_info");
        jdbcTemplate.update("DELETE FROM library");
        jdbcTemplate.update("DELETE FROM book");
        jdbcTemplate.update("DELETE FROM author");

        jdbcTemplate.update("ALTER TABLE author AUTO_INCREMENT = 1");
        jdbcTemplate.update("ALTER TABLE book AUTO_INCREMENT = 1");
        jdbcTemplate.update("ALTER TABLE library AUTO_INCREMENT = 1");
        jdbcTemplate.update("ALTER TABLE library_info AUTO_INCREMENT = 1");
    }

    private void seedDatabase() {
        LOGGER.info("Seeding database with test data...");

        jdbcTemplate.update("INSERT INTO author (id, first_name, last_name) VALUES (1, 'John', 'Doe')");
        jdbcTemplate.update("INSERT INTO author (id, first_name, last_name) VALUES (2, 'Jane', 'Smith')");
        jdbcTemplate.update("INSERT INTO author (id, first_name, last_name) VALUES (3, 'Emily', 'Johnson')");

        jdbcTemplate.update("INSERT INTO book (id, author_id, title, release_date) VALUES (1, 1, 'Book One by John', '2023-01-15')");
        jdbcTemplate.update("INSERT INTO book (id, author_id, title, release_date) VALUES (2, 1, 'Book Two by John', '2023-03-10')");
        jdbcTemplate.update("INSERT INTO book (id, author_id, title, release_date) VALUES (3, 2, 'Jane''s Journey', '2022-05-22')");
        jdbcTemplate.update("INSERT INTO book (id, author_id, title, release_date) VALUES (4, 3, 'Emily''s Adventures', '2021-12-05')");

        jdbcTemplate.update("INSERT INTO library (id, name) VALUES (1, 'Central Library')");
        jdbcTemplate.update("INSERT INTO library (id, name) VALUES (2, 'Community Library')");

        jdbcTemplate.update("INSERT INTO library_info (id, address, phone) VALUES (1, '123 Main St, Springfield', '555-1234')");
        jdbcTemplate.update("INSERT INTO library_info (id, address, phone) VALUES (2, '456 Elm St, Springfield', '555-5678')");

        jdbcTemplate.update("INSERT INTO library_book (library_id, book_id) VALUES (1, 1)");
        jdbcTemplate.update("INSERT INTO library_book (library_id, book_id) VALUES (1, 2)");
        jdbcTemplate.update("INSERT INTO library_book (library_id, book_id) VALUES (2, 2)");
        jdbcTemplate.update("INSERT INTO library_book (library_id, book_id) VALUES (2, 3)");
    }

    @DisplayName("TEST findById - Should return library with associated books & info")
    @Test
    void testFindById() {
        Optional<Library> optLib = libraryRepository.findById(1L);
        assertTrue(optLib.isPresent());
        Library library = optLib.get();

        assertEquals(1L, library.id());
        assertEquals("Central Library", library.name());
        assertNotNull(library.libraryInfo());
        assertEquals(1L, library.libraryInfo().id());
        assertEquals("123 Main St, Springfield", library.libraryInfo().address());
        assertEquals("555-1234", library.libraryInfo().phone());

        Set<Book> books = library.books();
        assertEquals(2, books.size());
    }

    @DisplayName("TEST findById - Should return Optional.empty if library not found")
    @Test
    void testFindByIdNotFound() {
        var result = libraryRepository.findById(999L);
        assertTrue(result.isEmpty());
    }

    @DisplayName("TEST findAll - Should return all libraries with their books")
    @Test
    void testFindAll() {
        var libraries = libraryRepository.findAll();
        assertFalse(libraries.isEmpty());
        assertEquals(2, libraries.size());

        var central = libraries.stream()
                .filter(lib -> lib.id() == 1L)
                .findFirst()
                .orElseThrow();
        assertEquals("Central Library", central.name());
        assertNotNull(central.libraryInfo());
        assertEquals("123 Main St, Springfield", central.libraryInfo().address());
        assertEquals("555-1234", central.libraryInfo().phone());
        assertEquals(2, central.books().size());

        var community = libraries.stream()
                .filter(lib -> lib.id() == 2L)
                .findFirst()
                .orElseThrow();
        assertEquals("Community Library", community.name());
        assertNotNull(community.libraryInfo());
        assertEquals("456 Elm St, Springfield", community.libraryInfo().address());
        assertEquals("555-5678", community.libraryInfo().phone());
        assertEquals(2, community.books().size());
    }

    @DisplayName("TEST findAll - Should return empty set if no libraries exist")
    @Test
    void testFindAllEmpty() {
        resetDatabase();
        var libraries = libraryRepository.findAll();
        assertTrue(libraries.isEmpty());
    }

    @DisplayName("TEST create - Should return newly inserted library ID")
    @Test
    void testCreate() {
        Library newLib = new Library(
                null,
                "New Library",
                new LibraryInfo(null, "789 Oak St, Springfield", "555-9999"),
                Set.of()
        );

        long newId = libraryRepository.create(newLib);
        assertTrue(newId > 0, "Created library ID should be > 0");

        Optional<Library> optLib = libraryRepository.findById(newId);
        assertTrue(optLib.isPresent(), "Newly created library should be found");
        Library created = optLib.get();

        assertEquals(newId, created.id());
        assertEquals("New Library", created.name());
        assertNotNull(created.libraryInfo());
        assertEquals("789 Oak St, Springfield", created.libraryInfo().address());
        assertEquals("555-9999", created.libraryInfo().phone());
        assertTrue(created.books().isEmpty());
    }

    @DisplayName("TEST update - Should update library name, address, phone")
    @Test
    void testUpdate() {
        Library updatedLib = new Library(
                1L,
                "Central Library Updated",
                new LibraryInfo(1L, "999 Updated St, Springfield", "555-0000"),
                Set.of()
        );

        libraryRepository.update(1L, updatedLib);

        Optional<Library> optLib = libraryRepository.findById(1L);
        assertTrue(optLib.isPresent());
        Library libAfterUpdate = optLib.get();

        assertEquals("Central Library Updated", libAfterUpdate.name());
        assertNotNull(libAfterUpdate.libraryInfo());
        assertEquals("999 Updated St, Springfield", libAfterUpdate.libraryInfo().address());
        assertEquals("555-0000", libAfterUpdate.libraryInfo().phone());
    }

    @DisplayName("TEST update - Non-existing library should not throw exception but not update anything")
    @Test
    void testUpdateNotFound() {
        Library nonExistentLib = new Library(
                999L,
                "Ghost Library",
                new LibraryInfo(999L, "NoWhere St", "999-9999"),
                Set.of()
        );

        libraryRepository.update(999L, nonExistentLib);

        Optional<Library> result = libraryRepository.findById(999L);
        assertTrue(result.isEmpty());
    }

    @DisplayName("TEST delete - Should delete library (and library_info) and return true")
    @Test
    void testDelete() {
        boolean deleted = libraryRepository.delete(1L);
        assertTrue(deleted, "Library with ID=1 should be deleted");

        Optional<Library> optLib = libraryRepository.findById(1L);
        assertTrue(optLib.isEmpty());
    }

    @DisplayName("TEST delete - Should return false if library doesn't exist")
    @Test
    void testDeleteNotFound() {
        boolean deleted = libraryRepository.delete(999L);
        assertFalse(deleted);
    }
}
