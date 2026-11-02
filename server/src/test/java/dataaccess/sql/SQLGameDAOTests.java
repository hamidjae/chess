package dataaccess.sql;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameDAOTests {
    private GameDAO gameDAO;

    @BeforeEach
    public void setup() throws DataAccessException{
        gameDAO = new SQLGameDAO();
        gameDAO.clear();
    }

    @Test
    public void createAndGetGame() throws DataAccessException{
        int gameID = gameDAO.createGame("Queen's Gambit");
        GameData storedGame = gameDAO.getGame(gameID);

        assertNotNull(storedGame);
        assertEquals(gameID, storedGame.gameID());
        assertEquals("Queen's Gambit", storedGame.gameName());
        assertNull(storedGame.whiteUsername());
        assertNull(storedGame.blackUsername());
        assertNotNull(storedGame.game());

        ChessGame expectedGame = new ChessGame();
        assertEquals(expectedGame.getBoard(), storedGame.game().getBoard());
        assertEquals(expectedGame.getTeamTurn(), storedGame.game().getTeamTurn());
    }

    @Test
    public void createGamesWithUniqueIDs() throws DataAccessException{
        int gameID = gameDAO.createGame("Levy Chess");
        int secondGameID = gameDAO.createGame("BongCloud");
        GameData storedGame = gameDAO.getGame(gameID);
        GameData secondStoredGame = gameDAO.getGame(secondGameID);

        assertNotNull(storedGame);
        assertNotNull(secondStoredGame);
        assertEquals("Levy Chess", storedGame.gameName());
        assertEquals("BongCloud", secondStoredGame.gameName());
        assertEquals(gameID, storedGame.gameID());
        assertEquals(secondGameID, secondStoredGame.gameID());
        assertNotEquals(gameID, secondGameID);
    }

    @Test
    public void rejectNullGameName() throws DataAccessException{
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(null));
        assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    public void getMissingGame() throws DataAccessException{
        assertNull(gameDAO.getGame(12312831));
    }

    @Test
    public void listNoGames() throws DataAccessException{
        assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    public void listMultipleGames() throws DataAccessException{
        int firstID = gameDAO.createGame("Queen's Gambit");
        int secondID = gameDAO.createGame("BongCloud");
        var games = gameDAO.listGames();

        GameData firstExpected = new GameData(
                firstID,
                null,
                null,
                "Queen's Gambit",
                new ChessGame());
        GameData secondExpected = new GameData(
                secondID,
                null,
                null,
                "BongCloud",
                new ChessGame());

        assertEquals(2, games.size());
        assertTrue(games.contains(firstExpected));
        assertTrue(games.contains(secondExpected));
    }

    @Test
    public void updateGameDetails() throws DataAccessException{
        int gameID = gameDAO.createGame("Winton");
        GameData storedGame = gameDAO.getGame(gameID);

        assertNull(storedGame.whiteUsername());
        assertNull(storedGame.blackUsername());
        assertEquals("Winton", storedGame.gameName());

        GameData existing = gameDAO.getGame(gameID);
        GameData updated = new GameData(
                existing.gameID(),
                "Hikaru",
                "Magnus",
                "World Finals",
                existing.game());

        gameDAO.updateGame(updated);
        GameData savedGame = gameDAO.getGame(gameID);

        assertNotNull(savedGame);
        assertEquals("Hikaru", savedGame.whiteUsername());
        assertEquals("Magnus", savedGame.blackUsername());
        assertEquals("World Finals", savedGame.gameName());
        assertEquals(existing.gameID(), savedGame.gameID());
        assertEquals(existing.game(), savedGame.game());
    }

}
