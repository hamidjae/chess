package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import result.CreateGameResult;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {

//    Create Game
//    Successful Game Creation

    @Test
    public void successfulCreateGame() throws DataAccessException, UnauthorizedException, BadRequestException{
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(gameDAO, authDAO);
        AuthData auth = new AuthData("testToken", "brandon");
        authDAO.createAuth(auth);

        CreateGameRequest request = new CreateGameRequest("testGame");
        CreateGameResult result = gameService.createGame(auth.authToken(), request);

        GameData storedGame = gameDAO.getGame(result.gameID());
        assertNotNull(storedGame);
        assertEquals("testGame", storedGame.gameName());
        assertNull(storedGame.blackUsername());
        assertNull(storedGame.whiteUsername());
        assertNotNull(storedGame.game());
    }

    @Test
    public void invalidTokenGame() throws DataAccessException, UnauthorizedException, BadRequestException{
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(gameDAO, authDAO);

        CreateGameRequest request = new CreateGameRequest("testGame2");
        assertThrows(UnauthorizedException.class, () -> gameService.createGame("random-kid-token", request));
        assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    public void missingGameName() throws DataAccessException, UnauthorizedException, BadRequestException{
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(gameDAO, authDAO);
        AuthData auth = new AuthData("testToken", "brandon");
        authDAO.createAuth(auth);

        CreateGameRequest request = new CreateGameRequest(null);
        assertThrows(BadRequestException.class, () -> gameService.createGame("testToken", request));
        assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    public void nullCreateGameRequest() throws DataAccessException, UnauthorizedException, BadRequestException{
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(gameDAO, authDAO);
        AuthData auth = new AuthData("testToken", "brandon");
        authDAO.createAuth(auth);

        assertThrows(BadRequestException.class, () -> gameService.createGame("testToken", null));
        assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    public void missingAuthToken() throws DataAccessException, UnauthorizedException, BadRequestException{
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(gameDAO, authDAO);
        AuthData auth = new AuthData("testToken", "brandon");
        authDAO.createAuth(auth);

        CreateGameRequest request = new CreateGameRequest("testGame");
        assertThrows(UnauthorizedException.class, () -> gameService.createGame(null, request));
        assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    public void uniqueGameIDs() throws DataAccessException, UnauthorizedException, BadRequestException{
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(gameDAO, authDAO);
        AuthData auth = new AuthData("testToken", "brandon");
        AuthData secondAuth = new AuthData("secondTestToken", "alexander");
        authDAO.createAuth(auth);
        authDAO.createAuth(secondAuth);

//        Brandon
        CreateGameRequest request = new CreateGameRequest("testGame");
        CreateGameResult result = gameService.createGame(auth.authToken(), request);
//        Alexander
        CreateGameRequest secondRequest = new CreateGameRequest("secondTestGame");
        CreateGameResult secondResult= gameService.createGame(secondAuth.authToken(), secondRequest);

        GameData firstGame = gameDAO.getGame(result.gameID());
        GameData secondGame = gameDAO.getGame(secondResult.gameID());

        assertNotNull(firstGame);
        assertNotNull(secondGame);
        assertEquals("testGame", firstGame.gameName());
        assertEquals("secondTestGame", secondGame.gameName());
        assertNotEquals(result.gameID(), secondResult.gameID());
    }
}
