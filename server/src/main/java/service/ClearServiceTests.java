package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTests {

    @Test
    public void clearRemovesAllData() throws DataAccessException {
//         Setting up storage with a user, login details and a game
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        userDAO.createUser(new UserData("hamid", "BYUrocks67", "hamidjjahangir@example.com"));
        authDAO.createAuth(new AuthData("testingtoken", "hamid"));
        int gameID = gameDAO.createGame("Test game");

        ClearService service = new ClearService(userDAO, gameDAO, authDAO);
        service.clear();

//        Checking to see that all data was removed
        assertNull(userDAO.getUser("hamid"));
        assertNull(authDAO.getAuth("test-token"));
        assertNull(gameDAO.getGame(gameID));
        assertTrue(gameDAO.listGames().isEmpty());
    }
}