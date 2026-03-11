package dataaccess;

import model.GameData;
import service.result.ImportantGameInfo;
import java.util.LinkedList;

public interface GameDAO {
    void clearGameData() throws DataAccessException;
    GameData addGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(GameData newGame) throws DataAccessException;
    LinkedList<ImportantGameInfo> getAllGames() throws DataAccessException;
}
