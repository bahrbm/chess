package service;

import dataaccess.*;
import exception.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private static final UserDAO EXISTING_USER_DAO = new MemoryUserDAO();
    private static final AuthDAO EXISTING_AUTH_DAO = new MemoryAuthDAO();
    private final UserService service = new UserService(userDAO,authDAO);
    private static UserService existingService = new UserService(EXISTING_USER_DAO, EXISTING_AUTH_DAO);
    private static String existingAuthToken;
    private static final String EXISTING_USERNAME = "user1";

    @BeforeAll
    static void setup() throws DataAccessException {
        RegisterRequest user = new RegisterRequest(EXISTING_USERNAME,"password","u1@mail");

        RegisterResult result = existingService.register(user);
        existingAuthToken = result.authToken();
    }

    @Test
    @Order(1)
    @DisplayName("Successful register")
    void register() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("a","A","a@mail");

        service.register(request);

        assertNotNull(userDAO.findByUsername("a"));
    }

    @Test
    @Order(2)
    @DisplayName("Register error: username taken")
    void registerBad() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("a","A","a@mail");
        RegisterRequest user2 = new RegisterRequest("a","newPassword","newUser@mail");

        service.register(request);
        assertNotNull(userDAO.findByUsername("a"));

        assertThrows(DataAccessException.class, () -> {
            service.register(user2);
        });
    }

    @Test
    @Order(3)
    @DisplayName("Successful Login")
    void login() throws DataAccessException {
        LoginRequest request = new LoginRequest("user1","password");

        LoginResult result = existingService.login(request);

        AuthData data = EXISTING_AUTH_DAO.findByAuthToken(result.authToken());

        assertEquals("user1",data.username());
    }

    @Test
    @Order(4)
    @DisplayName("Bad Password")
    void loginBadPassword() throws DataAccessException {
        LoginRequest request = new LoginRequest("user1","passw0rd");

        assertThrows(DataAccessException.class, () ->{
            existingService.login(request);
        });
    }

    @Test
    @Order(5)
    @DisplayName("Valid Token")
    void isAuthTokenValid() {
        assertDoesNotThrow(() -> {existingService.isAuthTokenValid(existingAuthToken);});
    }

    @Test
    @Order(6)
    @DisplayName("Invalid Token")
    void isAuthTokenNotValid() {
        String wrongToken = "I have a bad feeling about this";
        assertThrows(DataAccessException.class, () -> {existingService.isAuthTokenValid(wrongToken);});
    }

    @Test
    @Order(7)
    @DisplayName("User Exists")
    void getUser() throws DataAccessException {

        AuthData data = existingService.getUser(existingAuthToken);

        assertEquals(EXISTING_USERNAME,data.username());
    }

    @Test
    @Order(8)
    @DisplayName("User Doesn't Exist")
    void getBadUser() throws DataAccessException {

        assertThrows(DataAccessException.class, () ->{
           existingService.getUser("I have a bad feeling about this");
        });
    }

    @Test
    @Order(10)
    @DisplayName("Successful Logout")
    void logout() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("a","A","a@mail");

        RegisterResult result = service.register(request);

        LogoutRequest logoutRequest = new LogoutRequest(result.authToken());

        service.logout(logoutRequest);

        assertThrows(DataAccessException.class, () ->{
           service.isAuthTokenValid(result.authToken());
        });

    }

    @Test
    @Order(10)
    @DisplayName("Unsuccessful Logout")
    void logoutBad() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("a","A","a@mail");

        RegisterResult result = service.register(request);

        assertThrows(DataAccessException.class, () ->{
            service.isAuthTokenValid("I have a bad feeling about this");
        });

    }
}