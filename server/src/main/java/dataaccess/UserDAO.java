package dataaccess;

import model.UserData;

public interface UserDAO {

    void createUser(UserData u);

    UserData findByUsername(String username);

    void clearUserData();
}
