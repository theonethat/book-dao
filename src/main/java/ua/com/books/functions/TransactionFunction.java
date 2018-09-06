package ua.com.books.functions;

import java.sql.Connection;
import java.sql.SQLException;

public interface TransactionFunction<R> {

    R applyInTransaction(Connection connection) throws SQLException;
}
