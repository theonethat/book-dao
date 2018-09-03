package ua.com.books.dao;

import com.bobocode.util.FileReader;
import com.bobocode.util.JdbcUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import ua.com.books.model.Book;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;
import static ua.com.books.messages.ErrorMessages.ERROR_SAVING_ENTITY;

public class BookDaoTest {

    private static final String SQL_FILE_PATH = "db/create_tables.sql";
    private static BookDao bookDao;
    private static AuthorDao authorDao;
    private static TestEntityProvider testEntityProvider;

    @BeforeClass
    public static void init() throws SQLException {
        DataSource dataSource = JdbcUtil.createDefaultInMemoryH2DataSource();
        createTables(dataSource);
        authorDao = new AuthorDaoImpl(dataSource);
        bookDao = new BookDaoImpl(dataSource, authorDao);
        testEntityProvider = new TestEntityProvider();
    }

    private static void createTables(DataSource dataSource) throws SQLException {
        String createTablesSql = FileReader.readWholeFileFromResources(SQL_FILE_PATH);
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute(createTablesSql);
        }
    }

    @Test
    public void testSave() {
        Book testBook = testEntityProvider.generateBook();

        Long bookId = bookDao.save(testBook);

        List<Book> books = bookDao.findAll();
        assertNotNull(bookId);
        assertTrue(books.contains(testBook));
    }

    @Test
    public void testSaveBookWithoutIsbn() {
        List<Book> beforeSaveBooks = bookDao.findAll();
        Book testBook = testEntityProvider.generateBook();
        testBook.setIsbn(null);

        try {
            bookDao.save(testBook);
        } catch(Exception e) {
            assertEquals(RuntimeException.class, e.getClass());
            assertEquals(ERROR_SAVING_ENTITY.formatMessageWithClassName(testBook), e.getMessage());
        }

        List<Book> afterSaveBooks = bookDao.findAll();
        assertEquals(beforeSaveBooks.size(), afterSaveBooks.size());
    }

    @Test
    public void testFindAll() {
        List<Book> oldBooks = bookDao.findAll();
        List<Book> newBooks = testEntityProvider.generateBookList();
        newBooks.forEach(bookDao::save);

        List<Book> allBooks = bookDao.findAll();

        assertTrue(allBooks.containsAll(oldBooks));
        assertTrue(allBooks.containsAll(newBooks));
        assertEquals(oldBooks.size() + newBooks.size(), allBooks.size());
    }

    @Test
    public void testFindById() {
        Book testBook = testEntityProvider.generateBook();
        Long testBookId = bookDao.save(testBook);

        Book book = bookDao.findById(testBookId);

        assertEquals(testBook, book);
    }

    @Test
    public void testUpdate() {
        Book testBook = testEntityProvider.generateBook();
        Long bookId = bookDao.save(testBook);

        testBook.setTitle("New Title");
        testBook.setIsbn("978-0393064445");
        testBook.setPrice(BigDecimal.valueOf(35.15));
        testBook.setReleaseDate(LocalDate.of(2018, 9, 23));
        testBook.setLanguage("English");
        testBook.setWeight(100.20f);
        bookDao.update(testBook);

        Book updatedBook = bookDao.findById(bookId);

        assertEquals(testBook, updatedBook);
    }

    @Test
    public void testDelete() {
        Book testBook = testEntityProvider.generateBook();
        Long bookId = bookDao.save(testBook);
        List<Book> beforeDeleteBooks = bookDao.findAll();

        bookDao.delete(bookId);
        List<Book> afterDeleteBooks = bookDao.findAll();

        assertFalse(afterDeleteBooks.contains(testBook));
        assertEquals(beforeDeleteBooks.size() - 1, afterDeleteBooks.size());
    }

    @Test
    public void testFindBookByAuthorFirstName() {
        String authorFirstName = "Arthur C. Clarke";
        List<Book> booksByAuthor = testEntityProvider.generateBookList();
        for(Book book : booksByAuthor) {
            book.getAuthors().get(0).setFirstName(authorFirstName);
            bookDao.save(book);
        }

        List<Book> foundBooks = bookDao.findBooksByAuthorFirstName(authorFirstName);

        assertEquals(booksByAuthor.size(), foundBooks.size());
    }
}
