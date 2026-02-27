package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{

    private final HashMap<String, GameData> games = new HashMap<>();

    @Override
    public void clearGameData() {
        games.clear();
    }

    @Override
    public int addGame(String gameName) {

        int gameID = games.size() + 1;
        GameData newGame = new GameData(gameID,null,null,gameName, new ChessGame());

        games.put(String.valueOf(gameID),newGame);

        return gameID;
    }

    @Override
    public GameData getGame(int gameID) {
        return games.getOrDefault(String.valueOf(gameID),null);
    }

    @Override
    public void updateGame(GameData newGame) {
        int gameID = newGame.gameID();
        games.put(String.valueOf(gameID),newGame);
    }
}
