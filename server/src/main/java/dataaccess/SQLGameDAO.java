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
        configureDatabase();
        currID = 0;

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id FROM GameData";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while(rs.next()){
                        currID += 1;
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(DataAccessException.ErrorCode.ServerError, "Error: unable to access db");
        }
    }

    @Override
    public void clearGameData() throws DataAccessException {
        var statement = "TRUNCATE GameData";
        executeUpdate(statement);
        currID = 0;
    }

    @Override
    public GameData addGame(String gameName) throws DataAccessException {
        var statement = "INSERT INTO GameData (name, whiteUsername, blackUsername, json) VALUES (?, ?, ?, ?)";
        GameData newGame = new GameData(currID + 1, null, null, gameName, new ChessGame());
        String json = new Gson().toJson(newGame);
        executeUpdate(statement, gameName, null, null, json);
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

        executeUpdate(statement1, whiteUsername, id);
        executeUpdate(statement2, blackUsername, id);
        executeUpdate(statement3, json, id);
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

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p){
                        ps.setString(i + 1, p);
                    }
                    else if (param instanceof Integer p){
                        ps.setInt(i + 1, p);
                    }
                    else if (param == null){
                        ps.setNull(i + 1, NULL);
                    }
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if(rs.next()){
                    currID = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(DataAccessException.ErrorCode.ServerError, "Error: Unable to update db");
        }
    }

    private final String[] createStatements = {
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

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(DataAccessException.ErrorCode.ServerError, "Error: Unable to configure db");
        }
    }
}
