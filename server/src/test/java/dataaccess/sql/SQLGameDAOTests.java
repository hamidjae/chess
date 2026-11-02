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

}
