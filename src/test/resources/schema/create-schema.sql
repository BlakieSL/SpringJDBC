create table author
(
    id         bigint auto_increment
        primary key,
    first_name varchar(100) not null,
    last_name  varchar(100) not null
);

create table book
(
    id           bigint auto_increment
        primary key,
    author_id    bigint       not null,
    title        varchar(255) not null,
    release_date date         null,
    constraint book_ibfk_1
        foreign key (author_id) references author (id)
            on delete cascade
);

create index author_id
    on book (author_id);


create table library
(
    id   bigint       not null
        primary key,
    name varchar(255) not null
);

create table library_info
(
    id              bigint       not null
        primary key,
    address         varchar(255) not null,
    contact_number  varchar(15)  null,
    number_of_books int          null,
    constraint fk_library
        foreign key (id) references library (id)
);

