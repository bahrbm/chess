package service;

import dataaccess.*;
import model.AuthData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.request.CreateGameRequest;
import service.request.JoinGameRequest;
import service.request.RegisterRequest;
import service.result.ImportantGameInfo;
import service.result.ListGamesResult;
import service.result.RegisterResult;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    private final GameDAO gameDAO = new MemoryGameDAO();
    private static final AuthDAO EXISTING_AUTH_DAO = new MemoryAuthDAO();
    private static final GameDAO EXISTING_GAME_DAO = new MemoryGameDAO();
    private static final UserDAO EXISTING_USER_DAO = new MemoryUserDAO();
    private final GameService service = new GameService(gameDAO);
    private static GameService existingService = new GameService(EXISTING_GAME_DAO);
    private static UserService userService = new UserService(EXISTING_USER_DAO,EXISTING_AUTH_DAO);
    private static AuthData authData;

    @BeforeAll
    static void setup() throws DataAccessException {
        RegisterRequest register = new RegisterRequest("a","A","a@mail");
        RegisterResult result = userService.register(register);
        authData = new AuthData(result.username(),result.authToken());

        CreateGameRequest request = new CreateGameRequest("Existing");
        existingService.create(request);

    }

    @Test
    @DisplayName("Successful Game Creation")
    void create() throws DataAccessException {
        CreateGameRequest request1 = new CreateGameRequest("Best Game");
        CreateGameRequest request2 = new CreateGameRequest("Close One");
        CreateGameRequest request3 = new CreateGameRequest("Practice");

        service.create(request1);
        service.create(request2);
        service.create(request3);

        assertEquals(3,service.listGames().games().size());
    }

    @Test
    @DisplayName("Unsuccessful Game Creation")
    void createBad() throws DataAccessException {

        assertThrows(DataAccessException.class, () ->{
            service.create(new CreateGameRequest(null));
        });
    }

    @Test
    @DisplayName("Successful Game Join")
    void joinGame() throws DataAccessException {
        JoinGameRequest request = new JoinGameRequest("BLACK",1);
        existingService.joinGame(request, authData);

        LinkedList<ImportantGameInfo> games = EXISTING_GAME_DAO.getAllGames();

        assertEquals(authData.username(),games.getFirst().blackUsername());
    }

    @Test
    @DisplayName("Unsuccessful Game Join")
    void joinBadGame() throws DataAccessException {
        JoinGameRequest request = new JoinGameRequest("PURPLE",1);

        assertThrows(DataAccessException.class, () ->{
            existingService.joinGame(request,authData);
        });
    }

    @Test
    @DisplayName("List Games Works")
    void listGames() {

        ListGamesResult listOfGames = existingService.listGames();

        assertInstanceOf(LinkedList.class, listOfGames.games());

        LinkedList<ImportantGameInfo> games = (LinkedList<ImportantGameInfo>) listOfGames.games();
        assertNull(games.getFirst().whiteUsername());
        assertEquals("a",games.getFirst().blackUsername());
        assertEquals(1,games.getFirst().gameID());
        assertEquals("Existing",games.getFirst().gameName());
    }
}