package dataaccess;

import model.*;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    private final HashMap<String, AuthData> authorizedUsers = new HashMap<>();

    @Override
    public void createAuth(AuthData a){
        System.out.println(a.authToken());
        authorizedUsers.put(a.authToken(),a);
    }

    @Override
    public void clearAuthData() {
        authorizedUsers.clear();
    }

    @Override
    public AuthData findByAuthToken(String authToken){
        return authorizedUsers.getOrDefault(authToken,null);
    }

    @Override
    public void deleteAuth(String authToken){
        authorizedUsers.remove(authToken);
    }
}
