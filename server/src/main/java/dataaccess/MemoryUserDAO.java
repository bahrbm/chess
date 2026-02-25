package dataaccess;

import model.*;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    final private HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(UserData u){
        users.put(u.username(),u);
    }

    @Override
    public UserData findByUsername(String username) {
        return users.getOrDefault(username, null);
    }

    @Override
    public void clearUserData(){
        users.clear();
    }

    @Override
    public UserData getUser(String username){
        return users.get(username);
    }
}
