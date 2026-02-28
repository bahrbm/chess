package dataaccess;

import model.GameData;
import service.result.ImportantGameInfo;
import service.result.ListGamesResult;
import java.util.Collection;

public interface GameDAO {
    void clearGameData();
    int addGame(String gameName);
    GameData getGame(int gameID);
    void updateGame(GameData newGame);
    Collection<ImportantGameInfo> getAllGames();
}
