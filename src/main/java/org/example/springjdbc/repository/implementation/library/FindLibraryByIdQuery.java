package org.example.springjdbc.repository.implementation.library;

import org.example.springjdbc.model.Book;
import org.example.springjdbc.model.Library;
import org.example.springjdbc.model.LibraryInfo;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.example.springjdbc.helper.QueryStatements.FIND_LIBRARY_WITH_ASSOCIATIONS_BY_ID;

@Component
public class FindLibraryByIdQuery extends MappingSqlQuery<Library> {
    FindLibraryByIdQuery(DataSource ds) {
        super(ds, FIND_LIBRARY_WITH_ASSOCIATIONS_BY_ID);
        super.declareParameter(new SqlParameter(Types.BIGINT));
    }

    public Library findByIdAggregated(Long id) {
        List<Library> partials = super.execute(id);
        if (partials.isEmpty()) {
            return null;
        }
        Library base = partials.getFirst();

        Set<Book> allBooks = new HashSet<>(base.books());
        for (int i = 1; i < partials.size(); i++) {
            allBooks.addAll(partials.get(i).books());
        }

        return new Library(
                base.id(),
                base.name(),
                base.libraryInfo(),
                allBooks
        );
    }

    @Override
    protected Library mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long libraryId = rs.getLong("library_id");
        String libraryName = rs.getString("library_name");

        Long infoId = rs.getLong("library_info_id");
        String address = rs.getString("library_address");
        String phone = rs.getString("library_phone");

        long bookId = rs.getLong("book_id");
        String bookTitle = rs.getString("book_title");
        Date releaseDate = rs.getDate("book_release_date");

        LibraryInfo libraryInfo = new LibraryInfo(infoId, address, phone);

        Set<Book> books = new HashSet<>();
        if (!rs.wasNull() && bookId != 0) {
            books.add(new Book(
                    bookId,
                    null,
                    bookTitle,
                    (releaseDate != null) ? releaseDate.toLocalDate() : null,
                    Set.of()
            ));
        }

        return new Library(libraryId, libraryName, libraryInfo, books);
    }
}
