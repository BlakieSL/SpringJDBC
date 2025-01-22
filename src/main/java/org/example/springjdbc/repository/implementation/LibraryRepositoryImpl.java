package org.example.springjdbc.repository.implementation;

import org.example.springjdbc.model.Book;
import org.example.springjdbc.model.Library;
import org.example.springjdbc.model.LibraryInfo;
import org.example.springjdbc.repository.declaration.LibraryRepository;
import org.example.springjdbc.repository.implementation.library.*;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

/**
 * A repository implementation for the Library entity. It is done by delegating SQL operations
 * to specialized classes that uses specific abstractions: SqlUpdate, MappingSqlQuery for crud operations.
 */
@Repository("libraryRepository")
public class LibraryRepositoryImpl implements LibraryRepository {
    private static final Logger LOGGER = Logger.getLogger(LibraryRepositoryImpl.class.getName());
    private final FindLibraryByIdQuery findLibraryByIdQuery;
    private final FindAllLibrariesQuery findAllLibrariesQuery;
    private final InsertLibrary insertLibrary;
    private final InsertLibraryInfo insertLibraryInfo;
    private final UpdateLibrary updateLibrary;
    private final UpdateLibraryInfo updateLibraryInfo;
    private final DeleteLibrary deleteLibrary;
    private final DeleteLibraryInfo deleteLibraryInfo;

    public LibraryRepositoryImpl(FindLibraryByIdQuery findLibraryByIdQuery,
                                 FindAllLibrariesQuery findAllLibrariesQuery,
                                 InsertLibrary insertLibrary,
                                 InsertLibraryInfo insertLibraryInfo,
                                 UpdateLibrary updateLibrary,
                                 UpdateLibraryInfo updateLibraryInfo,
                                 DeleteLibrary deleteLibrary,
                                 DeleteLibraryInfo deleteLibraryInfo) {
        this.findLibraryByIdQuery = findLibraryByIdQuery;
        this.findAllLibrariesQuery = findAllLibrariesQuery;
        this.insertLibrary = insertLibrary;
        this.insertLibraryInfo = insertLibraryInfo;
        this.updateLibrary = updateLibrary;
        this.updateLibraryInfo = updateLibraryInfo;
        this.deleteLibrary = deleteLibrary;
        this.deleteLibraryInfo = deleteLibraryInfo;
    }

    @Override
    public Optional<Library> findById(Long id) {
        try {
            Library library = findLibraryByIdQuery.findByIdAggregated(id);
            return Optional.ofNullable(library);
        } catch (Exception e) {
            LOGGER.warning(e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Set<Library> findAll() {
        try {
            List<Library> partials = findAllLibrariesQuery.execute();
            if (partials.isEmpty()) {
                return Set.of();
            }

            Map<Long, Library> libraryMap = new HashMap<>();
            for (Library partial : partials) {
                libraryMap.merge(
                        partial.id(),
                        partial,
                        this::mergeLibraries
                );
            }
            return new HashSet<>(libraryMap.values());
        } catch (Exception e) {
            return Set.of();
        }
    }

    @Override
    public long create(Library library) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        insertLibrary.update(new Object[]{library.name()}, keyHolder);

        long generatedLibraryId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        insertLibraryInfo.update(
                generatedLibraryId,
                library.libraryInfo().address(),
                library.libraryInfo().phone()
        );

        return generatedLibraryId;
    }

    @Override
    public void update(long id, Library library) {
        updateLibrary.update(library.name(), id);

        updateLibraryInfo.update(
                library.libraryInfo().address(),
                library.libraryInfo().phone(),
                id
        );
    }

    @Override
    public boolean delete(long id) {
        deleteLibraryInfo.update(id);

        int rowsAffected = deleteLibrary.update(id);

        return rowsAffected > 0;
    }

    private Library mergeLibraries(Library existing, Library newPartial) {
        Set<Book> mergedBooks = new HashSet<>(existing.books());
        mergedBooks.addAll(newPartial.books());
        return new Library(
                existing.id(),
                existing.name(),
                existing.libraryInfo(),
                mergedBooks
        );
    }
}
