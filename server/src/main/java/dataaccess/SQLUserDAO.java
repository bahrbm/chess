package dataaccess;

import com.google.gson.Gson;
import exception.DataAccessException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class SQLUserDAO implements UserDAO{

    public SQLUserDAO() throws DataAccessException {
        String[] createStatements = {
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
        DatabaseManager.configureDatabase(createStatements);
    }

    @Override
    public void createUser(UserData u) throws DataAccessException {
        var statement = "INSERT INTO UserData (username, password, email, json) VALUES (?, ?, ?, ?)";
        String encryptedPassword = BCrypt.hashpw(u.password(), BCrypt.gensalt());
        UserData newUser = new UserData(u.username(), encryptedPassword, u.email());

        String json = new Gson().toJson(newUser);
        DatabaseManager.executeUpdate(statement, u.username(), encryptedPassword, u.email(), json);
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
            throw new DataAccessException(DataAccessException.ErrorCode.ServerError, "Error: unable to access db");
        }
        return null;
    }

    @Override
    public void clearUserData() throws DataAccessException {
        var statement = "TRUNCATE UserData";
        DatabaseManager.executeUpdate(statement);
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        return new Gson().fromJson(json, UserData.class);
    }

}
