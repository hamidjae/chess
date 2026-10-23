package server;

import dataaccess.DataAccessException;
import io.javalin.*;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryAuthDAO;
import service.ClearService;
import handler.ClearHandler;

public class Server {
    private final Javalin javalin;
    private final UserDAO userDAO = new MemoryUserDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);
    private final ClearHandler clearHandler = new ClearHandler(clearService);

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.delete("/db", context -> clearHandler.handle(context));
        javalin.exception(DataAccessException.class, (exception, context) -> {
            context.status(500);
            context.contentType("application/json");
                context.result("{ \"message\": \"Error: Could not access the stored data.\" }");
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
