package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import service.result.ImportantGameInfo;
import java.sql.*;
import java.util.LinkedList;

import static java.sql.Types.NULL;

public class SQLGameDAO implements GameDAO{

    private int currID;

    public SQLGameDAO() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS  GameData (
              `id` int NOT NULL AUTO_INCREMENT,
              `name` varchar(256) NOT NULL,
              `whiteUsername` varchar(256) ,
              `blackUsername` varchar(256) ,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(name)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };

        DatabaseManager.configureDatabase(createStatements);
        currID = 0;

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id FROM GameData";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    setID(rs);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(DataAccessException.ErrorCode.ServerError, "Error: unable to access db");
        }
    }

    @Override
    public void clearGameData() throws DataAccessException {
        var statement = "TRUNCATE GameData";
        DatabaseManager.executeUpdate(statement);
        currID = 0;
    }

    @Override
    public GameData addGame(String gameName) throws DataAccessException {
        var statement = "INSERT INTO GameData (name, whiteUsername, blackUsername, json) VALUES (?, ?, ?, ?)";
        GameData newGame = new GameData(currID + 1, null, null, gameName, new ChessGame());
        String json = new Gson().toJson(newGame);
        DatabaseManager.executeUpdate(statement, gameName, null, null, json);
        currID += 1;
        return newGame;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT json FROM GameData WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(DataAccessException.ErrorCode.ServerError, "Error: unable to access db");
        }
        return null;
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        return new Gson().fromJson(json, GameData.class);
    }

    @Override
    public void updateGame(GameData newGame) throws DataAccessException {
        int id = newGame.gameID();
        String blackUsername = newGame.blackUsername();
        String whiteUsername = newGame.whiteUsername();
        String json = new Gson().toJson(newGame);

        var statement1 = "UPDATE GameData SET whiteUsername = ? WHERE id = ?";
        var statement2 = "UPDATE GameData SET blackUsername = ? WHERE id = ?";
        var statement3 = "UPDATE GameData SET json = ? WHERE id = ?";

        DatabaseManager.executeUpdate(statement1, whiteUsername, id);
        DatabaseManager.executeUpdate(statement2, blackUsername, id);
        DatabaseManager.executeUpdate(statement3, json, id);
    }

    @Override
    public LinkedList<ImportantGameInfo> getAllGames() throws DataAccessException {

        LinkedList<ImportantGameInfo> games = new LinkedList<>();

        GameData currGame;
        for(int i = 1; i < currID + 1; i++){
            currGame = getGame(i);
            games.add(new ImportantGameInfo(i,currGame.whiteUsername(), currGame.blackUsername(), currGame.gameName()));
        }

        return games;
    }

    private void setID(ResultSet rs) throws SQLException {
        while(rs.next()){
            currID += 1;
        }
    }
}
