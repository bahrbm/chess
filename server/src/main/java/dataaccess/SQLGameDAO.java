package dataaccess;

import model.GameData;
import service.result.ImportantGameInfo;

import java.util.LinkedList;

public class SQLGameDAO implements GameDAO{
    @Override
    public void clearGameData() {

    }

    @Override
    public int addGame(String gameName) {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public void updateGame(GameData newGame) {

    }

    @Override
    public LinkedList<ImportantGameInfo> getAllGames() {
        return null;
    }
}
