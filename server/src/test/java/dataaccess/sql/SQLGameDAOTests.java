package dataaccess.sql;

import chess.*;
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

    @Test
    public void updateGamePreservesOtherGames() throws DataAccessException {
        int firstID = gameDAO.createGame("First Game");
        int secondID = gameDAO.createGame("Second Game");
        GameData secondBefore = gameDAO.getGame(secondID);

        GameData updated = new GameData(
                firstID, "Hikaru", "Magnus", "World Finals", new ChessGame());

        gameDAO.updateGame(updated);

        assertEquals(updated, gameDAO.getGame(firstID));
        assertEquals(secondBefore, gameDAO.getGame(secondID));
    }

    @Test
    public void updateGameWithNullPlayer() throws DataAccessException {
        int gameID = gameDAO.createGame("Player Test");
        ChessGame game = new ChessGame();

        gameDAO.updateGame(new GameData(
                gameID, "Hikaru", "Magnus", "Player Test", game));

        GameData updated = new GameData(
                gameID, null, "Magnus", "Player Test", game);
        gameDAO.updateGame(updated);

        GameData storedGame = gameDAO.getGame(gameID);

        assertNotNull(storedGame);
        assertNull(storedGame.whiteUsername());
        assertEquals("Magnus", storedGame.blackUsername());
        assertEquals(updated, storedGame);
    }

    @Test
    public void updateMissingGame() throws DataAccessException {
        GameData missingGame = new GameData(
                -1, "Hikaru", "Magnus", "Missing Game", new ChessGame());

        assertThrows(DataAccessException.class, () -> gameDAO.updateGame(missingGame));

        assertNull(gameDAO.getGame(-1));
        assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    public void rejectUpdateWithNullGameName() throws DataAccessException {
        int gameID = gameDAO.createGame("Original Name");
        GameData original = gameDAO.getGame(gameID);

        GameData invalid = new GameData(
                gameID, "Hikaru", "Magnus", null, original.game());

        assertThrows(DataAccessException.class, () -> gameDAO.updateGame(invalid));
        assertEquals(original, gameDAO.getGame(gameID));
    }

    @Test
    public void updateUnchangedGame() throws DataAccessException {
        int gameID = gameDAO.createGame("Unchanged Game");
        GameData original = gameDAO.getGame(gameID);

        assertDoesNotThrow(() -> gameDAO.updateGame(original));
        assertEquals(original, gameDAO.getGame(gameID));
    }

    @Test
    public void persistMovedPiece() throws DataAccessException, InvalidMoveException {
        int gameID = gameDAO.createGame("Pawn Move");
        GameData original = gameDAO.getGame(gameID);
        ChessGame game = original.game();

        ChessPosition start = new ChessPosition(2, 5);
        ChessPosition end = new ChessPosition(4, 5);
        game.makeMove(new ChessMove(start, end, null));

        GameData updated = new GameData(
                gameID, "Hikaru", "Magnus", "Pawn Move", game);
        gameDAO.updateGame(updated);

        GameData storedGame = gameDAO.getGame(gameID);

        assertNotNull(storedGame);
        assertNotNull(storedGame.game());
        assertNull(storedGame.game().getBoard().getPiece(start));
        assertEquals(
                new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                storedGame.game().getBoard().getPiece(end));
        assertEquals(game.getBoard(), storedGame.game().getBoard());
        assertEquals(ChessGame.TeamColor.BLACK, storedGame.game().getTeamTurn());
        assertEquals(updated, storedGame);

        assertTrue(gameDAO.listGames().contains(updated));
    }

    @Test
    public void gamePersistsAcrossDAOInstances() throws DataAccessException, InvalidMoveException {
        int gameID = gameDAO.createGame("Persistent Game");
        ChessGame game = new ChessGame();
        game.makeMove(new ChessMove(
                new ChessPosition(2, 4),
                new ChessPosition(4, 4),
                null));

        GameData updated = new GameData(
                gameID, "Hikaru", "Magnus", "Saved Game", game);
        gameDAO.updateGame(updated);

        GameDAO anotherDAO = new SQLGameDAO();
        GameData storedGame = anotherDAO.getGame(gameID);

        assertNotNull(storedGame);
        assertEquals(updated, storedGame);
        assertEquals(game.getBoard(), storedGame.game().getBoard());
        assertEquals(game.getTeamTurn(), storedGame.game().getTeamTurn());
    }

    @Test
    public void clearRemovesAllGames() throws DataAccessException {
        int firstID = gameDAO.createGame("First Game");
        int secondID = gameDAO.createGame("Second Game");

        gameDAO.clear();

        assertNull(gameDAO.getGame(firstID));
        assertNull(gameDAO.getGame(secondID));
        assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    public void clearEmptyTable() throws DataAccessException {
        assertDoesNotThrow(() -> gameDAO.clear());
        assertTrue(gameDAO.listGames().isEmpty());

        int gameID = gameDAO.createGame("After Clear");
        GameData storedGame = gameDAO.getGame(gameID);

        assertNotNull(storedGame);
        assertEquals("After Clear", storedGame.gameName());
        assertEquals(1, gameDAO.listGames().size());
    }

}
