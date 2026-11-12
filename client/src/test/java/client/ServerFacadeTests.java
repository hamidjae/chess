import client.ServerFacade;
import org.junit.jupiter.api.*;
import server.Server;

import java.io.IOException;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade(port);
        System.out.println("Started test HTTP server on " + port);
    }
    @BeforeEach
    void clearDatabase() throws IOException, InterruptedException{
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertNotNull(facade);
    }

    @Test
    void registerNewUser() throws Exception{
        var result = facade.register("player1", "testPassword", "player1@gmail.com");

        Assertions.assertEquals("player1", result.username());
        Assertions.assertNotNull(result.authToken());
        Assertions.assertFalse(result.authToken().isBlank());
    }

    @Test
    void duplicateUsername() throws IOException, InterruptedException{
        facade.register("player1", "password1", "player@gmail.com");
        var error = Assertions.assertThrows(IOException.class, () -> facade.register("player1",
                "password2", "playRrr@gmail.com"));
        Assertions.assertEquals("Username already taken.", error.getMessage());
    }

    @Test
    void loginUser() throws Exception{
        var registration = facade.register("player1", "testPassword", "player1@gmail.com");
        var result = facade.login("player1", "testPassword");

        Assertions.assertEquals("player1", registration.username());
        Assertions.assertEquals("player1", result.username());

        Assertions.assertNotNull(registration.authToken());
        Assertions.assertNotNull(result.authToken());

        Assertions.assertFalse(registration.authToken().isBlank());
        Assertions.assertFalse(result.authToken().isBlank());
    }

    @Test
    void rejectWrongPassword() throws Exception{
        var registration = facade.register("player1",
                "testPassword", "player1@gmail.com");

        var error = Assertions.assertThrows(IOException.class,
                () -> facade.login("player1", "wrong"));

        Assertions.assertEquals("The username doesn't exist or the password is wrong",
                error.getMessage());
    }

    @Test
    void logoutValidToken() throws IOException, InterruptedException {
        var registration = facade.register("player1",
                "testPassword", "player1@example.com");
        String token = registration.authToken();

        Assertions.assertDoesNotThrow(() -> facade.logout(token));

        var error = Assertions.assertThrows(IOException.class, () -> facade.logout(token));
        Assertions.assertEquals("Session is no longer valid.", error.getMessage());
    }

    @Test
    void rejectInvalidLogoutToken() {
        var error = Assertions.assertThrows(IOException.class,
                () -> facade.logout("random-kid"));

        Assertions.assertEquals("Session is no longer valid.", error.getMessage());
    }

    @Test
    void createGameReturnsGameID() throws Exception {
        var user = facade.register("player1", "testPassword", "player1@example.com");

        int gameID = facade.createGame(user.authToken(), "Queen's Gambit");

        Assertions.assertTrue(gameID > 0);
    }

    @Test
    void createGameRejectsInvalidToken() throws Exception {
        var user = facade.register("player1", "testPassword", "player1@example.com");

        var error = Assertions.assertThrows(IOException.class,
                () -> facade.createGame("invalid-token", "Unauthorized Game"));

        Assertions.assertEquals("Session is no longer valid.", error.getMessage());
        Assertions.assertTrue(facade.listGames(user.authToken()).games().isEmpty());
    }

    @Test
    void listGamesReturnsCreatedGames() throws Exception {
        var user = facade.register("player1", "testPassword", "player1@example.com");
        String token = user.authToken();
        int firstID = facade.createGame(token, "Queen's Gambit");
        int secondID = facade.createGame(token, "BongCloud");

        var games = facade.listGames(token).games();

        Assertions.assertEquals(2, games.size());
        Assertions.assertTrue(games.stream().anyMatch(game ->
                game.gameID() == firstID && "Queen's Gambit".equals(game.gameName())));
        Assertions.assertTrue(games.stream().anyMatch(game ->
                game.gameID() == secondID && "BongCloud".equals(game.gameName())));
    }

    @Test
    void listGamesRejectsInvalidToken() {
        var error = Assertions.assertThrows(IOException.class,
                () -> facade.listGames("invalid-token"));

        Assertions.assertEquals("Session is no longer valid.", error.getMessage());
    }

}
