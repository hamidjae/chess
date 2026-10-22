package dataaccess;

import java.util.*;

import model.GameData;
import chess.ChessGame;

public class MemoryGameDAO implements GameDAO{
    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextGameID = 1;

    @Override
    public int createGame(String gameName) throws DataAccessException{
        int gameID = nextGameID;
        nextGameID++;
        GameData game = new GameData(gameID, null, null, gameName, new ChessGame());
        games.put(gameID, game);
        return gameID;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException{
        if (games.containsKey(game.gameID())){
            games.put(game.gameID(), game);
        }
        else{
            throw new DataAccessException("Game not found");
        }
    }

    @Override
    public void clear() throws DataAccessException{
        games.clear();
    }
}
