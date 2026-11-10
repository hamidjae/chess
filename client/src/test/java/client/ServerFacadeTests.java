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

}
