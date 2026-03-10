package dataaccess;

import com.google.gson.Gson;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import static java.sql.Types.NULL;

public class SQLUserDAO implements UserDAO{

    public SQLUserDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void createUser(UserData u) throws DataAccessException {
        var statement = "INSERT INTO UserData (username, password, email, json) VALUES (?, ?, ?, ?)";
        String encryptedPassword = BCrypt.hashpw(u.password(), BCrypt.gensalt());
        UserData newUser = new UserData(u.username(), encryptedPassword, u.email());

        String json = new Gson().toJson(newUser);
        executeUpdate(statement, u.username(), encryptedPassword, u.email(), json);
    }

    @Override
    public UserData findByUsername(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT json FROM UserData WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(DataAccessException.ErrorCode.ServerError, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void clearUserData() throws DataAccessException {
        var statement = "TRUNCATE UserData";
        executeUpdate(statement);
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        return new Gson().fromJson(json, UserData.class);
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
            CREATE TABLE IF NOT EXISTS  UserData (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`username`),
              INDEX(password)
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
