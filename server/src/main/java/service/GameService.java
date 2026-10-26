package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.GameSummary;
import result.ListGamesResult;

import java.util.ArrayList;
import java.util.Collection;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest request)
            throws DataAccessException, UnauthorizedException, BadRequestException {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null || authToken == null){
            throw new UnauthorizedException("Error: unauthorized");
        }

        if (request == null || request.gameName() == null){
            throw new BadRequestException("Error: bad request");
        }

        int gameID = gameDAO.createGame(request.gameName());
        return new CreateGameResult(gameID);
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException, UnauthorizedException{
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null || authToken == null){
            throw new UnauthorizedException("Error: unauthorized");
        }

        Collection<GameData> games = gameDAO.listGames();
        Collection<GameSummary> summaries = new ArrayList<>();
        for (GameData game : games){
            GameSummary summary = new GameSummary(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName());
            summaries.add(summary);
        }

        return new ListGamesResult(summaries);
    }

    public void joinGame(String authToken, JoinGameRequest request)
            throws DataAccessException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null || authToken == null){
            throw new UnauthorizedException("Error: unauthorized");
        }

        if (request == null || request.gameID() == null || request.playerColor() == null){
            throw new BadRequestException("Error: bad request");
        }

        if ((!request.playerColor().equals("WHITE")) && !(request.playerColor().equals("BLACK"))){
            throw new BadRequestException("Error: bad request");
        }

        GameData game = gameDAO.getGame(request.gameID());

        if (game == null){
            throw new BadRequestException("Error: bad request");
        }

        GameData updatedGame;

        if (request.playerColor().equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new AlreadyTakenException("Error: already taken");
            }

            updatedGame = new GameData(game.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game());
        }

        else {
            if (game.blackUsername() != null) {
                throw new AlreadyTakenException("Error: already taken");
            }

            updatedGame = new GameData(game.gameID(), game.whiteUsername(), auth.username(), game.gameName(), game.game());
        }

        gameDAO.updateGame(updatedGame);
    }
}
