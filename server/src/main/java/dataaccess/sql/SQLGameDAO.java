package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAO implements GameDAO {
    public SQLGameDAO() throws DataAccessException{
        DatabaseManager.createDatabase();
        String statement = """
                CREATE TABLE IF NOT EXISTS games (
                gameID INT PRIMARY KEY AUTO_INCREMENT,
                whiteUsername VARCHAR(255),
                blackUsername VARCHAR(255),
                gameName VARCHAR(255) NOT NULL,
                game TEXT NOT NULL
                )
                """;

        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(statement)){
            preparedStatement.executeUpdate();
        }

        catch(SQLException exception){
            throw new DataAccessException("Failed to create game table", exception);
        }
    }

    @Override
    public int createGame(String gameName) throws DataAccessException{
        String statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (null, null, ?, ?)";
        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)){
            ChessGame game = new ChessGame();
            String gameJson = new Gson().toJson(game);
            preparedStatement.setString(1, gameName);
            preparedStatement.setString(2, gameJson);
            preparedStatement.executeUpdate();

            try (var generatedKeys = preparedStatement.getGeneratedKeys()){
                if (generatedKeys.next()){
                    return generatedKeys.getInt(1);
                }
                throw new DataAccessException("Failed to retrieve new game ID");
            }
        }

        catch (SQLException exception){
            throw new DataAccessException("Failed to create game", exception);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException{
        String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID = ?";

        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(statement)){
            preparedStatement.setInt(1, gameID);

            try (var results = preparedStatement.executeQuery()){
                if (results.next()){
                    String gameJson = results.getString("game");
                    ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);

                    return new GameData(
                            results.getInt("gameID"),
                            results.getString("whiteUsername"),
                            results.getString("blackUsername"),
                            results.getString("gameName"), game);
                }
                return null;
            }
        }

        catch(SQLException exception){
            throw new DataAccessException("Failed to get game", exception);
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException{
        Collection<GameData> games = new ArrayList<>();
        String statement = """
                SELECT gameID, whiteUsername, blackUsername, gameName, game
                FROM games
                """;

        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(statement)){

            try (var results = preparedStatement.executeQuery()){
                while (results.next()){
                    String gameJson = results.getString("game");
                    ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);

                    GameData storedGame = new GameData(
                            results.getInt("gameID"),
                            results.getString("whiteUsername"),
                            results.getString("blackUsername"),
                            results.getString("gameName"), game);

                    games.add(storedGame);
                }
            }
        }
        catch (SQLException exception){
            throw new DataAccessException("Failed to list games", exception);
        }

        return games;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException{
        String statement = """
                UPDATE games
                SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ?
                WHERE gameID = ?
                """;

        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(statement)){
            preparedStatement.setString(1, game.whiteUsername());
            preparedStatement.setString(2, game.blackUsername());
            preparedStatement.setString(3, game.gameName());
            preparedStatement.setString(4, new Gson().toJson(game.game()));
            preparedStatement.setInt(5, game.gameID());
            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows == 0){
                throw new DataAccessException("Game not found");
            }

        }
        catch(SQLException exception){
            throw new DataAccessException("Failed to update game", exception);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String statement = "DELETE FROM games";

        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        }
        catch (SQLException exception) {
            throw new DataAccessException("Failed to clear games", exception);
        }
    }
}
