delete from author;
delete from book;
delete from library;
delete from library_info;

alter table author AUTO_INCREMENT = 1;
alter table book AUTO_INCREMENT = 1;
alter table library AUTO_INCREMENT = 1;
alter table library_info AUTO_INCREMENT = 1;