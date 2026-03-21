package dataaccess;

import exception.DataAccessException;
import model.UserData;

public interface UserDAO {

    void createUser(UserData u) throws DataAccessException;

    UserData findByUsername(String username) throws DataAccessException;

    void clearUserData() throws DataAccessException;
}
