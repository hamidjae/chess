package server;

import dataaccess.DataAccessException;
import handler.UserHandler;
import io.javalin.*;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryAuthDAO;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.ClearService;
import handler.ClearHandler;
import service.UserService;

public class Server {
    private final Javalin javalin;
    private final UserDAO userDAO = new MemoryUserDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);
    private final ClearHandler clearHandler = new ClearHandler(clearService);
    private final UserService userService = new UserService(authDAO, userDAO);
    private final UserHandler userHandler = new UserHandler(userService);

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
//        Clear application
        javalin.delete("/db", context -> clearHandler.handle(context));
//        Register
        javalin.post("/user", context -> userHandler.register(context));

        javalin.exception(DataAccessException.class, (exception, context) -> {
            context.status(500);
            context.contentType("application/json");
            context.result("{ \"message\": \"Error: Could not access the stored data.\" }");
        });

        javalin.exception(BadRequestException.class, (exception, context) -> {
            context.status(400);
            context.contentType("application/json");
            context.result("{ \"message\": \"Error: bad request\" }");
        });

        javalin.exception(AlreadyTakenException.class, (exception, context) -> {
            context.status(403);
            context.contentType("application/json");
            context.result("{ \"message\": \"Error: already taken\" }");
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
