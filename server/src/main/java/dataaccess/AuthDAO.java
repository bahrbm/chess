package dataaccess;

import exception.DataAccessException;
import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData a) throws DataAccessException;
    void clearAuthData() throws DataAccessException;
    AuthData findByAuthToken(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
}
