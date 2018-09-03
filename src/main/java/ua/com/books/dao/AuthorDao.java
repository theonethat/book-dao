package ua.com.books.dao;

import ua.com.books.model.Author;

import java.util.List;

public interface AuthorDao {

    List<Long> save(List<Author> authors);

    Long save(Author author);

    List<Author> findByBookId(long bookId);
}
