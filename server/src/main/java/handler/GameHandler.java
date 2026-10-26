package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.GameService;
import service.UnauthorizedException;

public class GameHandler {
    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void createGame(Context context) throws BadRequestException, UnauthorizedException, DataAccessException {
        String authorizationToken = context.header("authorization");
        Gson gson = new Gson();
        CreateGameRequest request = gson.fromJson(context.body(), CreateGameRequest.class);
        CreateGameResult result = gameService.createGame(authorizationToken, request);
        context.contentType("application/json");
        context.result(gson.toJson(result));
    }

    public void listGames(Context context) throws UnauthorizedException, DataAccessException{
        String authorizationToken = context.header("authorization");
        Gson gson = new Gson();
        ListGamesResult result = gameService.listGames(authorizationToken);
        context.contentType("application/json");
        context.result(gson.toJson(result));
    }

    public void joinGame(Context context) throws DataAccessException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        String authToken = context.header("authorization");
        Gson gson = new Gson();
        JoinGameRequest request = gson.fromJson(context.body(), JoinGameRequest.class);
        gameService.joinGame(authToken, request);

        context.status(200);
        context.contentType("application/json");
        context.result("{}");
    }
}
