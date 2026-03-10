package dataaccess;

import com.google.gson.Gson;
import model.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Types.NULL;

public class SQLAuthDAO implements AuthDAO{

    public SQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void createAuth(AuthData a) throws DataAccessException {
        var statement = "INSERT INTO AuthData (authToken, username, json) VALUES (?, ?, ?)";
        String json = new Gson().toJson(a);
        executeUpdate(statement, a.authToken(), a.username(), json);
    }

    @Override
    public void clearAuthData() throws DataAccessException {
        var statement = "TRUNCATE AuthData";
        executeUpdate(statement);
    }

    @Override
    public AuthData findByAuthToken(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT json FROM AuthData WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(DataAccessException.ErrorCode.ServerError, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        return new Gson().fromJson(json, AuthData.class);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM AuthData WHERE authToken = ?";
        executeUpdate(statement, authToken);
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(DataAccessException.ErrorCode.ServerError, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  AuthData (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(username)
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
            throw new DataAccessException(DataAccessException.ErrorCode.ServerError, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
