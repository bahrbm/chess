package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData a);
    void clearAuthData();
    AuthData findByAuthToken(String authToken);
    void deleteAuth(String authToken);
}
