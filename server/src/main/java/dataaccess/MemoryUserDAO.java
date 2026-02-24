package dataaccess;

import model.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class MemoryUserDAO implements UserDAO{
    final private HashMap<String, Collection<String>> users = new HashMap<>();

    @Override
    public void createUser(UserData u) throws DataAccessException {
        users.put(u.username(),new LinkedList<>(Arrays.asList(u.password(),u.email())));

    }

}
