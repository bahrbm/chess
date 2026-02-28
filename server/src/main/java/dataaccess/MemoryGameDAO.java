package dataaccess;

import chess.ChessGame;
import model.GameData;
import service.result.ImportantGameInfo;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class MemoryGameDAO implements GameDAO{

    private final HashMap<String, GameData> games = new HashMap<>();
    private final LinkedList<ImportantGameInfo> listOfGames = new LinkedList<>();

    @Override
    public void clearGameData() {
        games.clear();
        listOfGames.clear();
    }

    @Override
    public int addGame(String gameName) {

        int gameID = games.size() + 1;
        GameData newGame = new GameData(gameID,null,null,gameName, new ChessGame());

        games.put(String.valueOf(gameID),newGame);
        listOfGames.add(new ImportantGameInfo(gameID, null, null, gameName));

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

        ImportantGameInfo gameChange = new ImportantGameInfo(gameID, newGame.whiteUsername(), newGame.blackUsername(), newGame.gameName());

        listOfGames.set(gameID-1, gameChange);
    }

    @Override
    public Collection<ImportantGameInfo> getAllGames() {
        return listOfGames;
    }
}
