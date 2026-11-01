package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO{
    public SQLAuthDAO() throws DataAccessException{
        DatabaseManager.createDatabase();
        String statement = """
                CREATE TABLE IF NOT EXISTS auth (
                authToken VARCHAR(255) PRIMARY KEY,
                username VARCHAR(255) NOT NULL
                )
                """;

        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(statement)){
            preparedStatement.executeUpdate();
        }
        catch(SQLException exception){
            throw new DataAccessException("Failed to create auth table", exception);
        }
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException{
        String statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(statement)){
            preparedStatement.setString(1, auth.authToken());
            preparedStatement.setString(2, auth.username());
            preparedStatement.executeUpdate();
        }
        catch (SQLException exception){
            throw new DataAccessException("Failed to create auth", exception);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String statement = "SELECT authToken, username FROM auth WHERE authToken = ?";

        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.setString(1, authToken);

            try (var results = preparedStatement.executeQuery()) {
                if (results.next()) {
                    return new AuthData(
                            results.getString("authToken"),
                            results.getString("username"));
                }
                return null;
            }
        }
        catch (SQLException exception) {
            throw new DataAccessException("Failed to get auth", exception);
        }
    }
}
