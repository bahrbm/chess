package service;

import dataaccess.*;
import exception.DataAccessException;
import model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {

    private final GameDAO gameDAO = new MemoryGameDAO();
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final ClearService service = new ClearService(userDAO,authDAO,gameDAO);

    @Test
    void clear() throws DataAccessException {
        UserData user = new UserData("a","A","a@mail");
        AuthData auth = new AuthData("a","asdfghjk;lasdf");

        userDAO.createUser(user);
        authDAO.createAuth(auth);
        gameDAO.addGame("best game");

        service.clear();

        assertNull(userDAO.findByUsername("a"));
        assertNull(authDAO.findByAuthToken("asdfghjk;lasdf"));
        assertEquals(0, gameDAO.getAllGames().size());
    }
}