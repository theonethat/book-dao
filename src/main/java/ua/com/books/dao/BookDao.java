package ua.com.books.dao;

import ua.com.books.model.Book;

import java.util.List;

public interface BookDao {

    Long save(Book book);

    List<Book> findAll();

    Book findById(Long id);

    void update(Book book);

    void delete(Long id);

    List<Book> findBooksByAuthorFirstName(String authorFirstName);

}
