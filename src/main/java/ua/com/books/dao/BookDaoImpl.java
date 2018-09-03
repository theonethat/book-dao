package ua.com.books.dao;

import ua.com.books.model.Book;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ua.com.books.dao.Sql.*;
import static ua.com.books.messages.ErrorMessages.*;

public class BookDaoImpl implements BookDao {
    private DataSource dataSource;
    private AuthorDao authorDao;
    private DaoSkeleton<Book> daoSkeleton;

    public BookDaoImpl(DataSource dataSource, AuthorDao authorDao) {
        this.daoSkeleton = new DaoSkeleton<>();
        this.dataSource = dataSource;
        this.authorDao = authorDao;
    }

    @Override
    public Long save(Book book) {
        try (Connection connection = dataSource.getConnection()) {
            return saveBookAndAuthors(connection, book);
        } catch(SQLException e) {
            throw new RuntimeException(ERROR_SAVING_ENTITY.formatMessageWithClassName(book), e);
        }
    }

    @Override
    public List<Book> findAll() {
        try (Connection connection = dataSource.getConnection()) {
            return findAllBooks(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public Book findById(Long id) {
        try (Connection connection = dataSource.getConnection()) {
            return findBookById(connection, id);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void update(Book book) {
        try (Connection connection = dataSource.getConnection()) {
            updateBook(connection, book);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long id) {
        try (Connection connection = dataSource.getConnection()) {
            deleteBook(connection, id);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public List<Book> findBooksByAuthorFirstName(String authorFirstName) {
        try (Connection connection = dataSource.getConnection()) {
            return findBooksByAuthorName(connection, authorFirstName);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private Long saveBookAndAuthors(Connection connection, Book book) throws SQLException {
        Long bookId = saveBook(connection, book);
        List<Long> authorsIds = authorDao.save(book.getAuthors());
        saveBookAuthor(connection, bookId, authorsIds);
        return bookId;
    }

    private Long saveBook(Connection connection, Book book) throws SQLException {
        Long bookId = daoSkeleton.saveEntity(connection, book, this::prepareInsertStatement);
        book.setId(bookId);
        return bookId;
    }

    private PreparedStatement prepareInsertStatement(Connection connection, Book book) throws SQLException {
        PreparedStatement insertStatement = connection.prepareStatement(INSERT_BOOK.query(), Statement.RETURN_GENERATED_KEYS);
        mapBook(insertStatement, book);
        return insertStatement;
    }

    private int mapBook(PreparedStatement statement, Book book) throws SQLException {
        int parameterIndex = 1;
        statement.setString(parameterIndex++, book.getTitle());
        statement.setString(parameterIndex++, book.getIsbn());
        statement.setBigDecimal(parameterIndex++, book.getPrice());
        statement.setDate(parameterIndex++, Date.valueOf(book.getReleaseDate()));
        statement.setString(parameterIndex++, book.getLanguage());
        statement.setDouble(parameterIndex++, book.getWeight());
        return parameterIndex;
    }

    private Long saveBookAuthor(Connection connection, long bookId, List<Long> authorsIds) throws SQLException {
        PreparedStatement insertStatement = prepareInsertStatement(connection, bookId, authorsIds);
        daoSkeleton.executeUpdate(insertStatement, authorsIds.size(), ENTITY_NOT_CREATED.formatMessage("book-author relation", bookId));
        return bookId;
    }

    private PreparedStatement prepareInsertStatement(Connection connection, long bookId, List<Long> authorsIds) throws SQLException {
        PreparedStatement insertStatement = connection.prepareStatement(INSERT_BOOK_AUTHOR.multipleInsertQuery(authorsIds.size()));
        mapBookAuthor(insertStatement, bookId, authorsIds);
        return insertStatement;
    }

    private void mapBookAuthor(PreparedStatement statement, long bookId, List<Long> authorsIds) throws SQLException {
        int parameterIndex = 1;
        for (Long authorId : authorsIds) {
            statement.setLong(parameterIndex++, bookId);
            statement.setLong(parameterIndex++, authorId);
        }
    }

    private List<Book> findAllBooks(Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(SELECT_ALL_BOOKS.query());
        ResultSet rs = statement.executeQuery();
        return collectToList(rs);
    }

    private List<Book> findBooksByAuthorName(Connection connection, String authorFirstName) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(SELECT_BOOKS_BY_AUTHOR_NAME.query());
        statement.setString(1, authorFirstName);
        ResultSet rs = statement.executeQuery();
        return collectToList(rs);
    }


    private List<Book> collectToList(ResultSet rs) throws SQLException {
        List<Book> books = new ArrayList<>();
        while (rs.next())
            books.add(parseRow(rs));
        return books;
    }

    private Book parseRow(ResultSet rs) throws SQLException {
        int columnIndex = 1;
        Book book = new Book();
        book.setId(rs.getLong(columnIndex++));
        book.setTitle((rs.getString(columnIndex++)));
        book.setIsbn(rs.getString(columnIndex++));
        book.setPrice(rs.getBigDecimal(columnIndex++));
        book.setReleaseDate(rs.getDate(columnIndex++).toLocalDate());
        book.setLanguage(rs.getString(columnIndex++));
        book.setWeight(rs.getFloat(columnIndex++));
        book.setAuthors(authorDao.findByBookId(book.getId()));
        return book;
    }

    private Book findBookById(Connection connection, Long id) throws SQLException {
        PreparedStatement selectByIdStatement = prepareSelectByIdStatement(connection, id);
        ResultSet rs = selectByIdStatement.executeQuery();
        return parseBook(rs, id);
    }

    private PreparedStatement prepareSelectByIdStatement(Connection connection, Long id) throws SQLException {
            PreparedStatement selectByIdStatement = connection.prepareStatement(SELECT_BOOK_BY_ID.query());
            selectByIdStatement.setLong(1, id);
            return selectByIdStatement;
    }

    private Book parseBook(ResultSet rs, Long id) throws SQLException {
        if (rs.next())
            return parseRow(rs);
        else
            throw new RuntimeException(BOOK_DOES_NOT_EXIST.formatMessage(id));
    }

    private void updateBook(Connection connection, Book book) throws SQLException {
        Objects.requireNonNull(book.getId(), BOOK_WITHOUT_ID::message);
        PreparedStatement updateBookStatement = prepareUpdateBookStatement(connection, book);
        daoSkeleton.executeUpdate(updateBookStatement, BOOK_DOES_NOT_EXIST.formatMessage(book.getId()));
    }

    private PreparedStatement prepareUpdateBookStatement(Connection connection, Book book) throws SQLException {
        PreparedStatement updateBookStatement = connection.prepareStatement(UPDATE_BOOK.query());
        int parameterIndex = mapBook(updateBookStatement, book);
        updateBookStatement.setLong(parameterIndex, book.getId());
        return updateBookStatement;
    }

    private void deleteBook(Connection connection, Long id) throws SQLException {
        Objects.requireNonNull(id, BOOK_WITHOUT_ID::message);
        PreparedStatement deleteStatement = prepareDeleteStatement(connection, DELETE_BOOK_AUTHOR.query(), id);
        deleteStatement.executeUpdate();
        deleteStatement = prepareDeleteStatement(connection, DELETE_BOOK.query(), id);
        daoSkeleton.executeUpdate(deleteStatement, BOOK_DOES_NOT_EXIST.formatMessage(id));
    }

    private PreparedStatement prepareDeleteStatement(Connection connection, String query, Long id) throws SQLException {
        PreparedStatement deleteStatement = connection.prepareStatement(query);
        deleteStatement.setLong(1, id);
        return deleteStatement;
    }
}
