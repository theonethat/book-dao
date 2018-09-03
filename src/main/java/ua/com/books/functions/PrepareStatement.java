package ua.com.books.functions;

import java.sql.SQLException;

public interface PrepareStatement<Connection, E, PreparedStatement> {

    PreparedStatement prepareStatment(Connection connection, E entity) throws SQLException;
}
