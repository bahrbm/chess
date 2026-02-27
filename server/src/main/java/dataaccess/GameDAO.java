package dataaccess;

import model.GameData;

public interface GameDAO {
    void clearGameData();
    int addGame(String gameName);
    GameData getGame(int gameID);
    void updateGame(GameData newGame);
}
