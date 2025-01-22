package org.example.springjdbc.repository.implementation.library;

import org.example.springjdbc.model.Book;
import org.example.springjdbc.model.Library;
import org.example.springjdbc.model.LibraryInfo;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashSet;
import java.util.Set;

import static org.example.springjdbc.helper.QueryStatements.FIND_ALL_LIBRARIES_WITH_ASSOCIATIONS;

@Component
public class FindAllLibrariesQuery extends MappingSqlQuery<Library> {
    FindAllLibrariesQuery(DataSource ds) {
        super(ds, FIND_ALL_LIBRARIES_WITH_ASSOCIATIONS);
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
