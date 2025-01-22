package org.example.springjdbc.helper;

public enum QueryStatements {
    ;
    public static final String FIND_AUTHOR_BY_ID = "SELECT * FROM author WHERE id = ?";
    public static final String FIND_AUTHOR_WITH_ASSOCIATIONS_BY_ID = """
        SELECT a.id AS author_id, a.first_name, a.last_name,
               b.id AS book_id, b.title, b.release_date
        FROM author a
        LEFT JOIN book b ON a.id = b.author_id
        WHERE a.id = ?
    """;
    public static final String ALL_SELECT = """
            SELECT
             a.id AS author_id, a.first_name, a.last_name,
             b.id AS book_id, b.title, b.release_date
            FROM AUTHOR a
            LEFT JOIN BOOK b ON a.id = b.author_id
            """;
    public static final String INSERT_AUTHOR = """
            INSERT INTO author (first_name, last_name)
            VALUES (?, ?)
            """;
    public static final String UPDATE_AUTHOR = """
            UPDATE author
            SET first_name = ?, last_name = ?
            WHERE id = ?
            """;
    public static final String DELETE_AUTHOR = """
            DELETE FROM author WHERE id = ?
            """;

    //Book repo
    public static final String FIND_BOOK_WITH_ASSOCIATIONS_BY_ID = """
            SELECT b.id AS book_id, 
                   b.author_id AS book_author_id, 
                   b.title AS book_title, 
                   b.release_date AS book_release_date, 
                   l.id AS library_id, 
                   l.name AS library_name
            FROM book b
            LEFT JOIN library_book lb ON b.id = lb.book_id
            LEFT JOIN library l ON lb.library_id = l.id
            WHERE b.id = ?
            """;

    public static final String SELECT_ALL_BOOKS = """
            "SELECT * FROM book"
            """;
    public static final String INSERT_BOOK = """
            INSERT INTO book (title, author_id, release_date) VALUES (?, ?, ?)
            """;
    public static final String UPDATE_BOOK = """
            UPDATE book SET title = ?, author_id = ?, release_date = ? WHERE id = ?
            """;
    public static final String DELETE_BOOK = """
            DELETE FROM book WHERE id = ?
            """;

    //library repo
    public static final String FIND_LIBRARY_WITH_ASSOCIATIONS_BY_ID = """
            SELECT l.id AS library_id,
                   l.name AS library_name,
                   li.id AS library_info_id,
                   li.address AS library_address,
                   li.phone AS library_phone,
                   b.id AS book_id,
                   b.author_id AS book_author_id,
                   b.title AS book_title,
                   b.release_date AS book_release_date
            FROM library l
            LEFT JOIN library_info li ON l.id = li.id
            LEFT JOIN library_book lb ON l.id = lb.library_id
            LEFT JOIN book b ON lb.book_id = b.id
            WHERE l.id = ?
            ORDER BY b.id;
            """;

    public static final String FIND_ALL_LIBRARIES_WITH_ASSOCIATIONS = """
        SELECT l.id AS library_id,
               l.name AS library_name,
               li.id AS library_info_id,
               li.address AS library_address,
               li.phone AS library_phone,
               b.id AS book_id,
               b.title AS book_title,
               b.release_date AS book_release_date
        FROM library l
        LEFT JOIN library_info li ON l.id = li.id
        LEFT JOIN library_book lb ON l.id = lb.library_id
        LEFT JOIN book b ON lb.book_id = b.id
    """;

    public static final String INSERT_LIBRARY = """
        INSERT INTO library (name) VALUES (?)
    """;

    public static final String INSERT_LIBRARY_INFO = """
        INSERT INTO library_info (id, address, phone) VALUES (?,?,?)
    """;

    public static final String UPDATE_LIBRARY = """
        UPDATE library
        SET name = ?
        WHERE id = ?
    """;

    public static final String UPDATE_LIBRARY_INFO = """
        UPDATE library_info
        SET address = ?, phone = ?
        WHERE id = ?
    """;

    public static final String DELETE_LIBRARY = """
        DELETE FROM library WHERE id = ?
    """;

    public static final String DELETE_LIBRARY_INFO = """
        DELETE FROM library_info WHERE id = ?
    """;
}
