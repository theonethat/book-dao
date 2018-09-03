package ua.com.books.dao;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

enum Sql {

    INSERT_BOOK( "insert into BOOKS(TITLE, ISBN, PRICE, RELEASE_DATE, LANGUAGE, WEIGHT) values(?, ?, ?, ?, ?, ?)"),
    INSERT_AUTHOR( "insert into AUTHORS(FIRST_NAME, MIDDLE_NAME, LAST_NAME, BIRTHDATE, EMAIL) values(?, ?, ?, ?, ?)"),
    INSERT_BOOK_AUTHOR( "insert into BOOKS_AUTHORS(BOOK_ID, AUTHOR_ID) values(?, ?)"),
    SELECT_ALL_BOOKS("select * from BOOKS"),
    SELECT_AUTHORS_BY_BOOK_ID("select a.* from AUTHORS a join BOOKS_AUTHORS ba on a.ID = ba.AUTHOR_ID where ba.BOOK_ID = ?"),
    SELECT_BOOK_BY_ID("select * from BOOKS where ID = ?"),
    SELECT_BOOKS_BY_AUTHOR_NAME("select * from BOOKS b " +
                                "join BOOKS_AUTHORS ab on b.ID = ab.BOOK_ID " +
                                "join AUTHORS a on a.ID = ab.AUTHOR_ID " +
                                "where a.FIRST_NAME = ?"),
    UPDATE_BOOK("update BOOKS set TITLE = ?, ISBN = ?, PRICE = ?, RELEASE_DATE = ?, LANGUAGE = ?, WEIGHT = ? where ID = ?"),
    DELETE_BOOK("delete from BOOKS where ID = ?; "),
    DELETE_BOOK_AUTHOR("delete from BOOKS_AUTHORS where BOOK_ID = ?;");

    private String query;

    Sql(String query) {
        this.query = query;
    }

    String query() {
        return query;
    }

    String multipleInsertQuery(int size) {
        String parametersQueryPart = query.substring(query.indexOf("(?"));
        String allParametersQueryPart = IntStream.range(1, size)
                .mapToObj(i -> parametersQueryPart)
                .collect(Collectors.joining(", "));
        String fullQuery = allParametersQueryPart.isEmpty() ? query : query + ", " + allParametersQueryPart;
        return fullQuery;
    }
}
