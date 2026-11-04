package server;

import dataaccess.DataAccessException;
import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLGameDAO;
import dataaccess.sql.SQLUserDAO;
import handler.GameHandler;
import handler.UserHandler;
import io.javalin.*;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import service.*;
import handler.ClearHandler;

public class Server {
    private final Javalin javalin;
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;
    private final ClearService clearService;
    private final ClearHandler clearHandler;
    private final UserService userService;
    private final UserHandler userHandler;
    private final GameService gameService;
    private final GameHandler gameHandler;

    public Server() {
        try {
            userDAO = new SQLUserDAO();
            gameDAO = new SQLGameDAO();
            authDAO = new SQLAuthDAO();
        }
        catch (DataAccessException exception) {
            throw new IllegalStateException("Failed to initialize database", exception);
        }

        clearService = new ClearService(userDAO, gameDAO, authDAO);
        clearHandler = new ClearHandler(clearService);

        userService = new UserService(authDAO, userDAO);
        userHandler = new UserHandler(userService);

        gameService = new GameService(gameDAO, authDAO);
        gameHandler = new GameHandler(gameService);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));
//        Clear application
        javalin.delete("/db", context -> clearHandler.handle(context));
//        Register
        javalin.post("/user", context -> userHandler.register(context));
//        Login
        javalin.post("/session", context -> userHandler.login(context));
//        Logout
        javalin.delete("/session", context -> userHandler.logout(context));
//        Create Game
        javalin.post("/game", context -> gameHandler.createGame(context));
//        List Games
        javalin.get("/game", context -> gameHandler.listGames(context));
//        Join Game
        javalin.put("/game", context -> gameHandler.joinGame(context));


//        Error 500: Description of error
        javalin.exception(DataAccessException.class, (exception, context) -> {
            context.status(500);
            context.contentType("application/json");
            context.result("{ \"message\": \"Error: Could not access the stored data.\" }");
        });

//        Error 400: BadRequestException
        javalin.exception(BadRequestException.class, (exception, context) -> {
            context.status(400);
            context.contentType("application/json");
            context.result("{ \"message\": \"Error: bad request\" }");
        });

//        Error 403: AlreadyTakenException
        javalin.exception(AlreadyTakenException.class, (exception, context) -> {
            context.status(403);
            context.contentType("application/json");
            context.result("{ \"message\": \"Error: already taken\" }");
        });

//        Error 401: UnauthorizedException
        javalin.exception(UnauthorizedException.class, (exception, context) ->{
            context.status(401);
            context.contentType("application/json");
            context.result("{ \"message\": \"Error: unauthorized\" }");
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
