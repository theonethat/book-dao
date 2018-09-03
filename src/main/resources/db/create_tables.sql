/*
Books database stores information about books and theirs authors.
Book can have many authors and obviously each author can write more than one book.

Book has title, isbn, price, release date, language, weight.
Restrictions:
- mandatory: title, isbn, release date, language
- unique: isbn
- isbn should not be longer than 17 symbols

Author has first, middle and last names, birthdate, email.
Restrictions:
- mandatory: first name, last name, birthdate, email
- unique: email

  TECH NOTES AND NAMING CONVENTION
- tables, columns and constraints should be named using "UPPER_CASE" naming convention
- table names must be plural (e.g. "BOOKS", not "BOOK")
- tables (except link tables) should have an ID field, which is a primary key.
- link tables should have a composite key that consists of two foreign key columns

- primary keys should be named according to the following convention "TABLE_NAME_PK"
- foreign keys should be named according to the following convention "TABLE_NAME_REFERENCE_TABLE_NAME_FK"
- alternative keys (unique) should be named according to the following convention "TABLE_NAME_COLUMN_NAME_UQ"
*/

create table BOOKS (
  ID            bigserial,
  TITLE         varchar not null,
  ISBN          varchar(17) not null,
  PRICE         numeric,
  RELEASE_DATE  date not null,
  LANGUAGE      varchar not null,
  WEIGHT        numeric,

  constraint BOOKS_PK primary key (ID),
  constraint BOOKS_ISBN_UQ unique (ISBN)
);

create table AUTHORS (
  ID          bigserial,
  FIRST_NAME  varchar not null,
  MIDDLE_NAME varchar,
  LAST_NAME   varchar not null,
  BIRTHDATE   date not null,
  EMAIL       varchar not null,

  constraint AUTHORS_PK       primary key (ID),
  constraint AUTHORS_EMAIL_UQ unique (EMAIL)
);

create table BOOKS_AUTHORS (
  BOOK_ID   bigint,
  AUTHOR_ID bigint,

  constraint BOOKS_AUTHORS_PK primary key (BOOK_ID, AUTHOR_ID),
  constraint BOOKS_AUTHORS_BOOKS_FK   foreign key (BOOK_ID)   references BOOKS (ID),
  constraint BOOKS_AUTHORS_AUTHORS_FK foreign key (AUTHOR_ID) references AUTHORS (ID)
);