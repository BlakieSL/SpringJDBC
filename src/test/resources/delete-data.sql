delete from example.author;
delete from example.book;
delete from example.library;
delete from example.library_info;

alter table example.author AUTO_INCREMENT = 1;
alter table example.book AUTO_INCREMENT = 1;
alter table example.library AUTO_INCREMENT = 1;
alter table example.library_info AUTO_INCREMENT = 1;