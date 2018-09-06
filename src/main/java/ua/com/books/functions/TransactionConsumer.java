package ua.com.books.functions;

import java.sql.Connection;
import java.sql.SQLException;

public interface TransactionConsumer {

    void executeInTrasaction(Connection connection) throws SQLException;
}
