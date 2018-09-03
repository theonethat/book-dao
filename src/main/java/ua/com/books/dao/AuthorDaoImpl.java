package ua.com.books.dao;

import ua.com.books.model.Author;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static ua.com.books.dao.Sql.INSERT_AUTHOR;
import static ua.com.books.dao.Sql.SELECT_AUTHORS_BY_BOOK_ID;
import static ua.com.books.messages.ErrorMessages.ERROR_SAVING_ENTITY;
import static ua.com.books.messages.ErrorMessages.ERROR_SAVING_LIST;

public class AuthorDaoImpl implements AuthorDao {
    private DaoSkeleton<Author> daoSkeleton;
    private DataSource dataSource;
    
    public AuthorDaoImpl(DataSource dataSource) {
        this.daoSkeleton = new DaoSkeleton<>();
        this.dataSource = dataSource;
    }

    @Override
    public List<Long> save(List<Author> authors) {
        try (Connection connection = dataSource.getConnection()) {
            return saveAuthors(connection, authors);
        } catch(SQLException e) {
            throw new RuntimeException(ERROR_SAVING_LIST.formatMessage("authors"), e);
        }
    }

    @Override
    public Long save(Author author) {
        try (Connection connection = dataSource.getConnection()) {
            return saveAuthor(connection, author);
        } catch(SQLException e) {
            throw new RuntimeException(ERROR_SAVING_ENTITY.formatMessageWithClassName(author), e);
        }
    }

    @Override
    public List<Author> findByBookId(long bookId) {
        try (Connection connection = dataSource.getConnection()) {
            return findAllAuthorsByBookId(connection, bookId);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private List<Long> saveAuthors(Connection connection, List<Author> authors) throws SQLException {
        List<Long> authorsIds = new ArrayList<>();
        for (Author author : authors) {
            Long aLong = saveAuthor(connection, author);
            authorsIds.add(aLong);
        }
        return authorsIds;
    }

    private Long saveAuthor(Connection connection, Author author) throws SQLException {
        Long authorId = daoSkeleton.saveEntity(connection, author, this::prepareInsertStatement);
        author.setId(authorId);
        return authorId;
    }

    private PreparedStatement prepareInsertStatement(Connection connection, Author author) throws SQLException {
        PreparedStatement insertStatement = connection.prepareStatement(INSERT_AUTHOR.query(), Statement.RETURN_GENERATED_KEYS);
        mapAuthor(insertStatement, author);
        return insertStatement;
    }

    private int mapAuthor(PreparedStatement statement, Author author) throws SQLException {
        int parameterIndex = 1;
        statement.setString(parameterIndex++, author.getFirstName());
        statement.setString(parameterIndex++, author.getMiddleName());
        statement.setString(parameterIndex++, author.getLastName());
        statement.setDate(parameterIndex++, Date.valueOf(author.getBirthDate()));
        statement.setString(parameterIndex++, author.getEmail());
        return parameterIndex;
    }

    private List<Author> findAllAuthorsByBookId(Connection connection, long bookId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(SELECT_AUTHORS_BY_BOOK_ID.query());
        statement.setLong(1, bookId);
        ResultSet rs = statement.executeQuery();
        return collectToList(rs);
    }

    private List<Author> collectToList(ResultSet rs) throws SQLException {
        List<Author> authors = new ArrayList<>();
        while (rs.next())
            authors.add(parseRow(rs));
        return authors;
    }

    private Author parseRow(ResultSet rs) throws SQLException {
        int columnIndex = 1;
        Author author = new Author();
        author.setId(rs.getLong(columnIndex++));
        author.setFirstName((rs.getString(columnIndex++)));
        author.setMiddleName(rs.getString(columnIndex++));
        author.setLastName(rs.getString(columnIndex++));
        author.setBirthDate(rs.getDate(columnIndex++).toLocalDate());
        author.setEmail(rs.getString(columnIndex++));

        return author;
    }
}
