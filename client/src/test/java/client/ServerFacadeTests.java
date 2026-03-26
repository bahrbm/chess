package client;

import dataaccess.*;
import exception.DataAccessException;
import org.junit.jupiter.api.*;
import request.*;
import server.Server;
import server.ServerFacade;
import service.ClearService;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private final AuthDAO authDAO = new SQLAuthDAO();
    private final GameDAO gameDAO = new SQLGameDAO();
    private final UserDAO userDAO = new SQLUserDAO();
    private final ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
    private static int port;

    public ServerFacadeTests() throws DataAccessException {
    }

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        String serverUrl = "http://localhost:" + port;
        facade = new ServerFacade(serverUrl);
    }

    @BeforeEach
    public void clearDB() throws DataAccessException {

        clearService.clear();
    }

    @AfterEach
    public void cleanup() throws DataAccessException {
        clearService.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void positiveRegisterTest() {
        RegisterRequest r = new RegisterRequest("test","test","test");
        assertDoesNotThrow(() -> facade.register(r));
    }

    @Test
    public void negativeRegisterTest() {
        RegisterRequest r = new RegisterRequest("test", "test", "test");

        assertDoesNotThrow(() -> facade.register(r));
        assertThrows(DataAccessException.class, () -> {
            facade.register(r);
        });
    }

    @Test
    public void positiveLoginTest(){
        RegisterRequest r = new RegisterRequest("test", "test", "test");
        LoginRequest l = new LoginRequest("test","test");
        assertDoesNotThrow(() -> facade.register(r));
        assertDoesNotThrow(() -> facade.login(l));
    }

    @Test
    public void negativeLoginTest() throws DataAccessException {
        RegisterRequest r = new RegisterRequest("test", "test", "test");
        LoginRequest l = new LoginRequest("test","badPassword");
        assertDoesNotThrow(() -> facade.register(r));
        assertThrows(DataAccessException.class, () -> facade.login(l));
    }

    @Test
    public void positiveLogoutTest() throws DataAccessException {
        RegisterRequest r = new RegisterRequest("test", "test", "test");

        String authToken = facade.register(r);

        LogoutRequest l = new LogoutRequest(authToken);
        assertDoesNotThrow(() -> facade.logout(l));
    }

    @Test
    public void negativeLogoutTest() throws DataAccessException {
        RegisterRequest r = new RegisterRequest("test", "test", "test");

        String authToken = facade.register(r);

        LogoutRequest l = new LogoutRequest("plz");
        assertThrows(DataAccessException.class, () -> facade.logout(l));
    }

    @Test
    public void positiveCreateGameTest() {
        RegisterRequest r = new RegisterRequest("test", "test", "test");
        CreateGameRequest cg = new CreateGameRequest("newGame");
        assertDoesNotThrow(() -> facade.register(r));
        assertDoesNotThrow(() -> facade.createGame(cg));
    }

    @Test
    public void negativeCreateGameTest(){
        CreateGameRequest cg = new CreateGameRequest("newGame");
        assertThrows(DataAccessException.class, () -> facade.createGame(cg));
    }

    @Test
    public void positiveListGameTest() throws DataAccessException {
        RegisterRequest r = new RegisterRequest("test", "test", "test");
        facade.register(r);
        facade.createGame(new CreateGameRequest("myGame"));
        facade.createGame(new CreateGameRequest("game2"));
        facade.createGame(new CreateGameRequest("Game3"));

        ListGamesRequest lg = new ListGamesRequest();
        assertDoesNotThrow(() -> facade.listGames(lg));
    }

    @Test
    public void negativeListGameTest() throws DataAccessException {
        ListGamesRequest lg = new ListGamesRequest();
        assertThrows(DataAccessException.class, () -> facade.listGames(lg));
    }

    @Test
    public void positiveJoinGameTest() throws DataAccessException {

        clearService.clear();

        ListGamesRequest lg = new ListGamesRequest();
        RegisterRequest r = new RegisterRequest("test", "test", "test");
        JoinGameRequest jg = new JoinGameRequest("BLACK", 1);
        assertDoesNotThrow(() -> facade.register(r));
        assertDoesNotThrow(() -> facade.createGame(new CreateGameRequest("newGame")));
        assertDoesNotThrow(() -> facade.listGames(lg));
        assertDoesNotThrow(() -> facade.joinGame(jg));
    }

    @Test
    public void negativeJoinGameTest(){
        JoinGameRequest jg = new JoinGameRequest("BLACK", 1);
        assertThrows(DataAccessException.class, () -> facade.joinGame(jg));
    }

}
