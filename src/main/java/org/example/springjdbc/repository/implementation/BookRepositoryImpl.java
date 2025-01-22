package org.example.springjdbc.repository.implementation;

import org.example.springjdbc.model.Book;
import org.example.springjdbc.model.Library;
import org.example.springjdbc.repository.declaration.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.example.springjdbc.helper.QueryStatements.*;

/**
    * This class implements the BookRepository interface using RowMapper<T>, ResultSetExtractor<T> with jdbcTemplate.
 */
@Repository("bookRepository")
public class BookRepositoryImpl implements BookRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(BookRepositoryImpl.class);

    public BookRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Book> findByIdWithAssociations(Long id) {
        try {
            return jdbcTemplate.query(FIND_BOOK_WITH_ASSOCIATIONS_BY_ID, new BookWithAssociationsExtractor(), id);
        } catch (Exception e) {
            LOGGER.error("Problem when executing SELECT!", e);
            return Optional.empty();
        }
    }

    @Override
    public Set<Book> findAll() {
        return new HashSet<>(jdbcTemplate.query(SELECT_ALL_BOOKS, new BookRowMapper()));
    }

    @Override
    public Book create(Book book) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            int rowsAffected = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_BOOK, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, book.title());
                ps.setLong(2, book.authorId());
                ps.setObject(3, book.releaseDate());
                return ps;
            }, keyHolder);

            if (rowsAffected > 0) {
                Long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();
                return new Book(generatedId, book.authorId(), book.title(), book.releaseDate(), Set.of());
            }
            throw new RuntimeException("Failed to create book");
        } catch (Exception e) {
            LOGGER.error("Problem when executing INSERT!", e);
            throw new RuntimeException("Failed to create book", e);
        }
    }

    @Override
    public Book update(long id, Book book) {
        try {
            int rowsAffected = jdbcTemplate.update(UPDATE_BOOK, book.title(), book.authorId(), book.releaseDate(), id);
            if (rowsAffected > 0) {
                return new Book(id, book.authorId(), book.title(), book.releaseDate(), Set.of());
            }
            throw new RuntimeException("No rows updated");
        } catch (Exception e) {
            LOGGER.error("Problem when executing UPDATE!", e);
            throw new RuntimeException("Failed to update book", e);
        }
    }

    @Override
    public boolean delete(long id) {
        try {
            int rowsAffected = jdbcTemplate.update(DELETE_BOOK, id);
            return rowsAffected > 0;
        } catch (Exception e) {
            LOGGER.error("Problem when executing DELETE!", e);
            throw new RuntimeException("Failed to delete book", e);
        }
    }

    private static class BookRowMapper implements RowMapper<Book> {
        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Book(
                    rs.getLong("id"),
                    rs.getLong("author_id"),
                    rs.getString("title"),
                    rs.getDate("release_date") != null
                            ? rs.getDate("release_date").toLocalDate()
                            : null,
                    Set.of()
            );
        }
    }

    private static class BookWithAssociationsExtractor implements ResultSetExtractor<Optional<Book>> {
        @Override
        public Optional<Book> extractData(ResultSet rs) throws SQLException {
            Book book = null;
            Map<Long, Library> libraryMap = new HashMap<>();

            while (rs.next()) {
                if (book == null) {
                    book = new Book(
                            rs.getLong("book_id"),
                            rs.getLong("book_author_id"),
                            rs.getString("book_title"),
                            rs.getDate("book_release_date") != null
                                    ? rs.getDate("book_release_date").toLocalDate()
                                    : null,
                            new HashSet<>()
                    );
                }

                Long libraryId = rs.getLong("library_id");
                if (libraryId != 0) {
                    libraryMap.putIfAbsent(libraryId, new Library(
                            libraryId,
                            rs.getString("library_name"),
                            null,
                            new HashSet<>()
                    ));
                }
            }

            if (book != null) {
                book.libraries().addAll(libraryMap.values());
            }

            return Optional.ofNullable(book);
        }
    }

}
