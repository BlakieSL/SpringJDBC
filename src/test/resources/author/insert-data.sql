
INSERT INTO example.author (id, first_name, last_name)
VALUES
    (1, 'John', 'Doe'),
    (2, 'Jane', 'Smith'),
    (3, 'Emily', 'Johnson');

-- Insert books
INSERT INTO example.book (id, author_id, title, release_date)
VALUES
    (1, 1, 'Book One by John', '2023-01-15'),
    (2, 1, 'Book Two by John', '2023-03-10'),
    (3, 2, 'Jane\'s Journey', '2022-05-22'),
    (4, 3, 'Emily\'s Adventures', '2021-12-05');
