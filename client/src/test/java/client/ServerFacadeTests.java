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

}
