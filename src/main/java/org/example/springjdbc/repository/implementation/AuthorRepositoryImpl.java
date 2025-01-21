package org.example.springjdbc.repository.implementation;

import org.example.springjdbc.model.Author;
import org.example.springjdbc.model.Book;
import org.example.springjdbc.repository.declaration.AuthorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import static org.example.springjdbc.helper.QueryStatements.*;

/**
    * This class implements the AuthorRepository interface using jdbcTemplate to interact
    * with the database and manual lambas instead of RowMapper.
 */
@Repository("authorRepository")
public class AuthorRepositoryImpl implements AuthorRepository {
    private JdbcTemplate jdbcTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorRepositoryImpl.class);

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Author> findByIdWithoutAssociations(long id) {
        try {
            Author author = jdbcTemplate.queryForObject(
                    FIND_AUTHOR_BY_ID,
                    (rs, rowNum) ->
                        new Author(
                                rs.getLong("id"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                Set.of()
                        ),
                    id
            );
            return Optional.ofNullable(author);
        } catch (Exception e) {
            LOGGER.error("An error occurred while trying to find author with id: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Author> findByIdWithAssociations(long id) {
        try {
            return jdbcTemplate.query(FIND_AUTHOR_WITH_ASSOCIATIONS_BY_ID, rs -> {
                Author author = null;
                Set<Book> books = new HashSet<>();

                while (rs.next()) {
                    if (author == null) {
                        author = new Author(
                                rs.getLong("author_id"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                books
                        );
                    }

                    Long bookId = rs.getLong("book_id");
                    if (!rs.wasNull()) {
                        books.add(new Book(
                                bookId,
                                rs.getLong("author_id"),
                                rs.getString("title"),
                                rs.getDate("release_date") != null
                                        ? rs.getDate("release_date").toLocalDate()
                                        : null
                        ));
                    }
                }

                return Optional.ofNullable(author);
            }, id);
        } catch (Exception e) {
            LOGGER.error("An error occurred while trying to find author with id: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public Set<Author> findAll() {
        try {
            return jdbcTemplate.query(ALL_SELECT, rs -> {
                Map<Long, Author> authorsMap = new HashMap<>();

                while (rs.next()) {
                    Long authorId = rs.getLong("author_id");

                    Author author = authorsMap.computeIfAbsent(authorId, id -> {
                        try {
                            return new Author(
                                    id,
                                    rs.getString("first_name"),
                                    rs.getString("last_name"),
                                    new HashSet<>()
                            );
                        } catch (SQLException e) {
                            throw new RuntimeException("Error mapping author", e);
                        }
                    });

                    Long bookId = rs.getLong("book_id");
                    if (!rs.wasNull()) {
                        author.books().add(new Book(
                                bookId,
                                authorId,
                                rs.getString("title"),
                                rs.getDate("release_date") != null
                                        ? rs.getDate("release_date").toLocalDate()
                                        : null
                        ));
                    }
                }

                return new HashSet<>(authorsMap.values());
            });
        } catch (Exception e) {
            LOGGER.error("An error occurred while fetching all authors with associations", e);
            return Set.of();
        }
    }

    @Override
    public Author create(Author author) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            int rowsAffected = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_AUTHOR, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, author.firstName());
                ps.setString(2, author.lastName());
                return ps;
            }, keyHolder);

            if (rowsAffected > 0) {
                Long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();
                return new Author(generatedId, author.firstName(), author.lastName(), Set.of());
            }
            throw new RuntimeException("Failed to create author");
        } catch (Exception e) {
            LOGGER.error("An error occurred while creating author: {}", author, e);
            throw new RuntimeException("Failed to create author", e);
        }
    }

    @Override
    public Author update(long id, Author author) {
        try {
            int rowsAffected = jdbcTemplate.update(UPDATE_AUTHOR, author.firstName(), author.lastName(), id);
            if (rowsAffected > 0) {
                return new Author(id, author.firstName(), author.lastName(), author.books());
            }
            throw new RuntimeException("No rows updated");
        } catch (Exception e) {
            LOGGER.error("An error occurred while updating author with id: {}", id, e);
            throw new RuntimeException("Failed to update author", e);
        }
    }

    @Override
    public boolean delete(long id) {
        try {
            int rowsAffected = jdbcTemplate.update(DELETE_AUTHOR, id);
            return rowsAffected > 0;
        } catch (Exception e) {
            LOGGER.error("An error occurred while deleting author with id: {}", id, e);
            return false;
        }
    }
}
