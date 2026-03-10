package dataaccess;

import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    final private HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(UserData u){

        // Create new UserData object with the hashed password to be stored in the database
        String hashedPassword = BCrypt.hashpw(u.password(), BCrypt.gensalt());
        UserData newUser = new UserData(u.username(), hashedPassword, u.email());

        users.put(u.username(),newUser);
    }

    @Override
    public UserData findByUsername(String username) {
        return users.getOrDefault(username, null);
    }

    @Override
    public void clearUserData(){
        users.clear();
    }
}
