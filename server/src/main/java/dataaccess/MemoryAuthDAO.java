package dataaccess;

import model.*;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    final private HashMap<String, AuthData> users = new HashMap<>();

    @Override
    public void createAuth(AuthData a){
        users.put(a.authToken(),a);
    }
}
