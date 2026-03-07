package dataaccess;

import model.AuthData;

public class SQLAuthDAO implements AuthDAO{
    @Override
    public void createAuth(AuthData a) {

    }

    @Override
    public void clearAuthData() {

    }

    @Override
    public AuthData findByAuthToken(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {

    }
}
