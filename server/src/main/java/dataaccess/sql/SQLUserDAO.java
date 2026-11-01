package dataaccess.sql;

import dataaccess.UserDAO;
import model.UserData;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {
    public SQLUserDAO() throws DataAccessException{
        DatabaseManager.createDatabase();
        String statement = """
                CREATE TABLE IF NOT EXISTS users (
                username VARCHAR(255) PRIMARY KEY,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL
                )
                """;
        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        }
        catch (SQLException exception) {
            throw new DataAccessException("Failed to create users table", exception);
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.setString(1, user.username());
            preparedStatement.setString(2, user.password());
            preparedStatement.setString(3, user.email());
            preparedStatement.executeUpdate();
        }
        catch (SQLException exception) {
            throw new DataAccessException("Failed to create user", exception);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String statement = "SELECT username, password, email FROM users WHERE username = ?";

        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.setString(1, username);

            try (var results = preparedStatement.executeQuery()) {
                if (results.next()) {
                    return new UserData(
                            results.getString("username"),
                            results.getString("password"),
                            results.getString("email"));
                }
                return null;
            }
        }
        catch (SQLException exception) {
            throw new DataAccessException("Failed to get user", exception);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String statement = "DELETE FROM users";

        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        }
        catch (SQLException exception) {
            throw new DataAccessException("Failed to clear users", exception);
        }
    }
}
