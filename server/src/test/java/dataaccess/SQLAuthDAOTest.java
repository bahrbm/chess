package dataaccess;

import exception.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SQLAuthDAOTest {

    AuthDAO authDAO  = new SQLAuthDAO();
    AuthData userOne = new AuthData("A","sampleAuth");
    AuthData userTwo = new AuthData("B","authTwo");

    SQLAuthDAOTest() throws DataAccessException {
    }

    @AfterEach
    void clearDB() throws DataAccessException {
        authDAO.clearAuthData();
    }

    @Test
    @Order(1)
    void clearAuthData() throws DataAccessException {
        authDAO.createAuth(userOne);
        authDAO.createAuth(userTwo);

        assertNotNull(authDAO.findByAuthToken("sampleAuth"));
        assertNotNull(authDAO.findByAuthToken("authTwo"));

        authDAO.clearAuthData();

        assertNull(authDAO.findByAuthToken("sampleAuth"));
        assertNull(authDAO.findByAuthToken("authTwo"));
    }

    @Test
    @Order(2)
    void createAuth() throws DataAccessException {
        authDAO.createAuth(userOne);

        assertNotNull(authDAO.findByAuthToken("sampleAuth"));
    }

    @Test
    @Order(3)
    void findByAuthToken() throws DataAccessException {
        authDAO.createAuth(userOne);
        authDAO.createAuth(userTwo);

        AuthData one = authDAO.findByAuthToken("sampleAuth");
        AuthData two = authDAO.findByAuthToken("authTwo");

        assertEquals("A", one.username());
        assertEquals("B", two.username());
    }

    @Test
    @Order(4)
    void deleteAuth() throws DataAccessException {
        authDAO.createAuth(userOne);
        authDAO.createAuth(userTwo);

        authDAO.deleteAuth("sampleAuth");

        assertNotNull(authDAO.findByAuthToken("authTwo"));
        assertNull(authDAO.findByAuthToken("sampleAuth"));
    }

    @Test
    @Order(5)
    void createAuthError() throws DataAccessException {
        authDAO.createAuth(userOne);

        assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(userOne);
        });
    }

    @Test
    @Order(6)
    void findByAuthTokenError() throws DataAccessException {
        assertNull(authDAO.findByAuthToken("Bad Auth Token"));
    }

    @Test
    @Order(7)
    void deleteAuthError() throws DataAccessException{
        authDAO.createAuth(userOne);
        authDAO.createAuth(userTwo);

        authDAO.deleteAuth("Not a Good Auth Token");

        assertNotNull(authDAO.findByAuthToken("sampleAuth"));
        assertNotNull(authDAO.findByAuthToken("authTwo"));
    }
}