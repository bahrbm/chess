package dataaccess;

import model.*;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    private final HashMap<String, AuthData> authorizedUsers = new HashMap<>();

    @Override
    public void createAuth(AuthData a){
        authorizedUsers.put(a.authToken(),a);
    }

    @Override
    public void clearAuthData() {
        authorizedUsers.clear();
    }
}
