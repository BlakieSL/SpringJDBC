package org.example.springjdbc.repository.implementation;

import org.example.springjdbc.model.Book;
import org.example.springjdbc.repository.declaration.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
    * This class implements the BookRepository interface using RowMapper<T> with jdbcTemplate.
 */
@Repository("bookRepository")
public class BookRepositoryImpl implements BookRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(BookRepositoryImpl.class);

    public BookRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Book> findById(Long id) {
        String sql = "SELECT * FROM book WHERE id = ?";
        try {
            Book book = jdbcTemplate.queryForObject(sql, new BookRowMapper(), id);
            return Optional.ofNullable(book);
        } catch (Exception e) {
            LOGGER.error("Problem when executing SELECT!", e);
            return Optional.empty();
        }
    }

    @Override
    public Set<Book> findAll() {
        String sql = "SELECT * FROM book";
        return new HashSet<>(jdbcTemplate.query(sql, new BookRowMapper()));
    }

    @Override
    public Book create(Book book) {
        String sql = "INSERT INTO book (title, author_id, release_date) VALUES (?, ?, ?)";
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            int rowsAffected = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, book.title());
                ps.setLong(2, book.authorId());
                ps.setObject(3, book.releaseDate());
                return ps;
            }, keyHolder);

            if (rowsAffected > 0) {
                Long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();
                return new Book(generatedId, book.authorId(), book.title(), book.releaseDate());
            }
            throw new RuntimeException("Failed to create book");
        } catch (Exception e) {
            LOGGER.error("Problem when executing INSERT!", e);
            throw new RuntimeException("Failed to create book", e);
        }
    }

    @Override
    public Book update(long id, Book book) {
        String sql = "UPDATE book SET title = ?, author_id = ?, release_date = ? WHERE id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, book.title(), book.authorId(), book.releaseDate(), id);
            if (rowsAffected > 0) {
                return new Book(id, book.authorId(), book.title(), book.releaseDate());
            }
            throw new RuntimeException("No rows updated");
        } catch (Exception e) {
            LOGGER.error("Problem when executing UPDATE!", e);
            throw new RuntimeException("Failed to update book", e);
        }
    }

    @Override
    public boolean delete(long id) {
        String sql = "DELETE FROM book WHERE id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, id);
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
                            : null
            );
        }
    }
}
