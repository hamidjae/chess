package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.GameSummary;
import result.ListGamesResult;

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

//    List Games

    @Test
    public void listNoGames() throws DataAccessException, UnauthorizedException, BadRequestException{
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(gameDAO, authDAO);
        AuthData auth = new AuthData("testToken", "michelangelo");
        authDAO.createAuth(auth);

        ListGamesResult result = gameService.listGames(auth.authToken());
        assertTrue(result.games().isEmpty());
    }

    @Test
    public void listMultipleGames() throws DataAccessException, UnauthorizedException, BadRequestException{
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(gameDAO, authDAO);
        AuthData auth = new AuthData("testToken", "leonardo");
        AuthData secondAuth = new AuthData("secondTestToken", "raphael");
        authDAO.createAuth(auth);
        authDAO.createAuth(secondAuth);

        CreateGameRequest request = new CreateGameRequest("testGame");
        CreateGameRequest secondRequest = new CreateGameRequest("secondTestGame");
        CreateGameResult result = gameService.createGame(auth.authToken(), request);
        CreateGameResult secondResult= gameService.createGame(secondAuth.authToken(), secondRequest);

        ListGamesResult gameList = gameService.listGames(auth.authToken());
        assertEquals(2, gameList.games().size());
        assertTrue(gameList.games().contains(new GameSummary(result.gameID(), null, null, "testGame")));
        assertTrue(gameList.games().contains(new GameSummary(secondResult.gameID(), null, null, "secondTestGame")));
    }

    @Test
    public void listGamesInvalidToken() throws DataAccessException, UnauthorizedException, BadRequestException{
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(gameDAO, authDAO);

        assertThrows(UnauthorizedException.class, () -> gameService.listGames("random-kid-token-2"));
    }

    @Test
    public void listGamesMissingToken() throws DataAccessException, UnauthorizedException, BadRequestException{
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        GameService gameService = new GameService(gameDAO, authDAO);

        assertThrows(UnauthorizedException.class, () -> gameService.listGames(null));
    }

//    Join Game

    private GameService setupJoinTest(GameDAO gameDAO, AuthDAO authDAO) throws DataAccessException {
        authDAO.createAuth(new AuthData("first-token", "leonardo"));
        authDAO.createAuth(new AuthData("second-token", "raphael"));
        return new GameService(gameDAO, authDAO);
    }

    @Test
    public void joinGameAsWhite() throws Exception {
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameService service = setupJoinTest(gameDAO, authDAO);
        int gameID = gameDAO.createGame("testGame");

        service.joinGame("first-token", new JoinGameRequest("WHITE", gameID));

        GameData game = gameDAO.getGame(gameID);
        assertEquals("leonardo", game.whiteUsername());
        assertNull(game.blackUsername());
    }

    @Test
    public void joinGameAsBlack() throws Exception {
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameService service = setupJoinTest(gameDAO, authDAO);
        int gameID = gameDAO.createGame("testGame");

        service.joinGame("first-token", new JoinGameRequest("BLACK", gameID));

        GameData game = gameDAO.getGame(gameID);
        assertEquals("leonardo", game.blackUsername());
        assertNull(game.whiteUsername());
    }

    @Test
    public void joinPreservesOtherPlayer() throws Exception {
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameService service = setupJoinTest(gameDAO, authDAO);
        int gameID = gameDAO.createGame("testGame");
        GameData originalGame = gameDAO.getGame(gameID);
        String originalState = new com.google.gson.Gson().toJson(originalGame.game());

        service.joinGame("first-token", new JoinGameRequest("WHITE", gameID));
        service.joinGame("second-token", new JoinGameRequest("BLACK", gameID));

        GameData game = gameDAO.getGame(gameID);
        assertEquals("leonardo", game.whiteUsername());
        assertEquals("raphael", game.blackUsername());
        assertEquals(gameID, game.gameID());
        assertEquals("testGame", game.gameName());
        assertEquals(originalState, new com.google.gson.Gson().toJson(game.game()));
    }

    @Test
    public void joinWhiteAlreadyTaken() throws Exception {
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameService service = setupJoinTest(gameDAO, authDAO);
        int gameID = gameDAO.createGame("testGame");

        service.joinGame("first-token", new JoinGameRequest("WHITE", gameID));

        assertThrows(AlreadyTakenException.class, () -> service.joinGame("second-token", new JoinGameRequest("WHITE", gameID)));

        GameData game = gameDAO.getGame(gameID);
        assertEquals("leonardo", game.whiteUsername());
        assertNull(game.blackUsername());
    }

    @Test
    public void joinBlackAlreadyTaken() throws Exception {
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameService service = setupJoinTest(gameDAO, authDAO);
        int gameID = gameDAO.createGame("testGame");

        service.joinGame("first-token", new JoinGameRequest("BLACK", gameID));

        assertThrows(AlreadyTakenException.class, () -> service.joinGame("second-token", new JoinGameRequest("BLACK", gameID)));

        GameData game = gameDAO.getGame(gameID);
        assertEquals("leonardo", game.blackUsername());
        assertNull(game.whiteUsername());
    }

    @Test
    public void joinWithInvalidToken() throws Exception {
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameService service = setupJoinTest(gameDAO, authDAO);
        int gameID = gameDAO.createGame("testGame");

        assertThrows(UnauthorizedException.class, () -> service.joinGame("unknown-token", new JoinGameRequest("WHITE", gameID)));
        assertPlayerSlotsEmpty(gameDAO, gameID);
    }

    @Test
    public void joinWithMissingToken() throws Exception {
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameService service = setupJoinTest(gameDAO, authDAO);
        int gameID = gameDAO.createGame("testGame");

        assertThrows(UnauthorizedException.class, () -> service.joinGame(null, new JoinGameRequest("WHITE", gameID)));
        assertPlayerSlotsEmpty(gameDAO, gameID);
    }

    @Test
    public void joinNonexistentGame() throws Exception {
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameService service = setupJoinTest(gameDAO, authDAO);

        assertThrows(BadRequestException.class, () -> service.joinGame("first-token", new JoinGameRequest("WHITE", 999)));
        assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    public void joinWithInvalidColor() throws Exception {
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameService service = setupJoinTest(gameDAO, authDAO);
        int gameID = gameDAO.createGame("testGame");

        assertThrows(BadRequestException.class, () -> service.joinGame("first-token", new JoinGameRequest("RED", gameID)));
        assertPlayerSlotsEmpty(gameDAO, gameID);
    }

    @Test
    public void joinWithMissingColor() throws Exception {
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameService service = setupJoinTest(gameDAO, authDAO);
        int gameID = gameDAO.createGame("testGame");

        assertThrows(BadRequestException.class, () -> service.joinGame("first-token", new JoinGameRequest(null, gameID)));
        assertPlayerSlotsEmpty(gameDAO, gameID);
    }

    @Test
    public void joinWithMissingGameID() throws Exception {
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameService service = setupJoinTest(gameDAO, authDAO);
        int gameID = gameDAO.createGame("testGame");

        assertThrows(BadRequestException.class, () -> service.joinGame("first-token", new JoinGameRequest("WHITE", null)));
        assertPlayerSlotsEmpty(gameDAO, gameID);
    }

    @Test
    public void joinWithNullRequest() throws Exception {
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameService service = setupJoinTest(gameDAO, authDAO);
        int gameID = gameDAO.createGame("testGame");

        assertThrows(BadRequestException.class, () -> service.joinGame("first-token", null));
        assertPlayerSlotsEmpty(gameDAO, gameID);
    }

    private void assertPlayerSlotsEmpty(GameDAO gameDAO, int gameID) throws DataAccessException {GameData game = gameDAO.getGame(gameID);
        assertNotNull(game);
        assertNull(game.whiteUsername());
        assertNull(game.blackUsername());
    }
}
