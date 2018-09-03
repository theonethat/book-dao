package ua.com.books.dao;

import ua.com.books.functions.PrepareStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static ua.com.books.messages.ErrorMessages.CANNOT_OBTAIN_ID;
import static ua.com.books.messages.ErrorMessages.ENTITY_NOT_CREATED;

class DaoSkeleton<E> {

    Long fetchGeneratedId(PreparedStatement statement, String errorMessage) throws SQLException {
        ResultSet generatedKeys = statement.getGeneratedKeys();

        if (generatedKeys.next())
            return generatedKeys.getLong(1);
        else
            throw new RuntimeException(errorMessage);
    }

    void executeUpdate(PreparedStatement statement, String errorMessage) throws SQLException {
        int rowsAffected = statement.executeUpdate();
        if (rowsAffected != 1)
            throw new RuntimeException(errorMessage);
    }

    void executeUpdate(PreparedStatement statement, int size, String errorMessage) throws SQLException {
        int rowsAffected = statement.executeUpdate();
        if (rowsAffected != size)
            throw new RuntimeException(errorMessage);
    }

    Long saveEntity(Connection connection, E entity,
                               PrepareStatement<Connection, E, PreparedStatement> preparedStatementFunction) throws SQLException {
        PreparedStatement insertStatement = preparedStatementFunction.prepareStatment(connection, entity);
        executeUpdate(insertStatement, ENTITY_NOT_CREATED.formatMessageWithClassName(entity));
        Long entityId = fetchGeneratedId(insertStatement, CANNOT_OBTAIN_ID.formatMessageWithClassName(entity));
        return entityId;
    }
}
