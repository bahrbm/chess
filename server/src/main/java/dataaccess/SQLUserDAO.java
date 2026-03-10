package dataaccess;

import com.google.gson.Gson;
import model.UserData;
import java.sql.*;

public class SQLUserDAO implements UserDAO{

    public SQLUserDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void createUser(UserData u) {
//        var statement = "INSERT INTO user (username, password, email) VALUES (?,?,?)";
//        executeUpdate(statement, u.username(),u.password(),u.email());
    }

    @Override
    public UserData findByUsername(String username) {
        return null;
    }

    @Override
    public void clearUserData() {

    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    ps.setString(i+1, (String) param);
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(DataAccessException.ErrorCode.ServerError, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
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
