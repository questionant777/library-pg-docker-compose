drop table if exists author;
drop sequence if exists author_seq;
create sequence author_seq;
create table author(id bigint DEFAULT nextval('author_seq') primary key, name varchar(255));

drop table if exists genre;
drop sequence if exists genre_seq;
create sequence genre_seq;
create table genre(id bigint DEFAULT nextval('genre_seq') primary key, name varchar(255));

drop table if exists book;
drop sequence if exists book_seq;
create sequence book_seq;
create table book(id bigint DEFAULT nextval('book_seq') primary key, name varchar(255), authorid bigint, genreid bigint);

drop table if exists bookcomment;
drop sequence if exists bookcomment_seq;
create sequence bookcomment_seq;
create table bookcomment(id bigint DEFAULT nextval('bookcomment_seq') primary key, comment varchar(255), bookid bigint);
