package dataaccess;

import exception.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

class SQLUserDAOTest {

    private final UserDAO userDAO = new SQLUserDAO();
    private final UserData userOne = new UserData("A","a","a@mail.com");
    private final UserData userTwo = new UserData("B","b","b@mail.com");
    private final UserData userThree = new UserData("C","c","c@mail.com");

    SQLUserDAOTest() throws DataAccessException {
    }

    @AfterEach
    void clearDB() throws DataAccessException {
        userDAO.clearUserData();
    }

    @Test
    void clearUserData() throws DataAccessException {
        userDAO.createUser(userOne);
        userDAO.createUser(userTwo);
        userDAO.createUser(userThree);

        userDAO.clearUserData();

        assertNull(userDAO.findByUsername("A"));
        assertNull(userDAO.findByUsername("B"));
        assertNull(userDAO.findByUsername("C"));
    }

    @Test
    void createUser() throws DataAccessException {
        userDAO.createUser(userOne);
        userDAO.createUser(userTwo);
        userDAO.createUser(userThree);

        assertNotNull(userDAO.findByUsername("A"));
        assertNotNull(userDAO.findByUsername("B"));
        assertNotNull(userDAO.findByUsername("C"));
    }

    @Test
    void findByUsername() throws DataAccessException {
        userDAO.createUser(userOne);

        UserData user = userDAO.findByUsername("A");
        String hashedPassword = user.password();

        assertEquals("A", user.username());
        assertTrue(BCrypt.checkpw("a", hashedPassword));
        assertEquals("a@mail.com",user.email());
    }

    @Test
    void createUserError() throws DataAccessException {
        userDAO.createUser(userOne);

        assertThrows(DataAccessException.class, () ->{
            userDAO.createUser(userOne);
        });
    }

    @Test
    void findByUsernameError() throws DataAccessException{
        userDAO.createUser(userOne);

        UserData u = userDAO.findByUsername("Wrong Username");

        assertNull(u);
    }
}