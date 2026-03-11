package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import service.result.ImportantGameInfo;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class SQLGameDAOTest {

    private final GameDAO gameDAO = new SQLGameDAO();

    SQLGameDAOTest() throws DataAccessException {
    }

    @AfterEach
    void clearDB() throws DataAccessException {
        gameDAO.clearGameData();
    }

    @Test
    void clearGameData() throws DataAccessException {
        GameData gameOne = gameDAO.addGame("Game 1");
        GameData gameTwo =gameDAO.addGame("Game 2");
        GameData gameThree =gameDAO.addGame("Game 3");

        assertNotNull(gameDAO.getGame(gameOne.gameID()));
        assertNotNull(gameDAO.getGame(gameTwo.gameID()));
        assertNotNull(gameDAO.getGame(gameThree.gameID()));

        gameDAO.clearGameData();

        assertNull(gameDAO.getGame(gameOne.gameID()));
        assertNull(gameDAO.getGame(gameTwo.gameID()));
        assertNull(gameDAO.getGame(gameThree.gameID()));
    }

    @Test
    void addGame() throws DataAccessException {
        GameData gameOne = gameDAO.addGame("Game 1");

        assertEquals(1,gameOne.gameID());
        assertNull(gameOne.blackUsername());
        assertNull(gameOne.whiteUsername());
        assertNotNull(gameDAO.getGame(gameOne.gameID()));
    }

    @Test
    void getGame() throws DataAccessException {
        GameData gameOne = gameDAO.addGame("Game 1");

        GameData test = gameDAO.getGame(gameOne.gameID());

        assertEquals(gameOne.gameID(),test.gameID());
        assertEquals(gameOne.gameName(),test.gameName());
        assertEquals(gameOne.whiteUsername(),test.whiteUsername());
        assertEquals(gameOne.blackUsername(),test.blackUsername());
        assertEquals(gameOne.game(),test.game());
    }

    @Test
    void updateGame() throws DataAccessException {
        GameData gameOne = gameDAO.addGame("Game 1");

        GameData test = gameDAO.getGame(gameOne.gameID());

        assertEquals(gameOne.gameID(),test.gameID());
        assertEquals(gameOne.gameName(),test.gameName());
        assertEquals(gameOne.whiteUsername(),test.whiteUsername());
        assertEquals(gameOne.blackUsername(),test.blackUsername());
        assertEquals(gameOne.game(),test.game());

        GameData updateOne = new GameData(gameOne.gameID(),"UserOne","UserTwo",gameOne.gameName(),gameOne.game());

        gameDAO.updateGame(updateOne);

        test = gameDAO.getGame(gameOne.gameID());

        assertEquals(updateOne.gameID(),test.gameID());
        assertEquals(updateOne.gameName(),test.gameName());
        assertEquals(updateOne.whiteUsername(),test.whiteUsername());
        assertEquals(updateOne.blackUsername(),test.blackUsername());
        assertEquals(updateOne.game(),test.game());
    }

    @Test
    void getAllGames() throws DataAccessException {
        gameDAO.addGame("Game 1");
        LinkedList<ImportantGameInfo> games = gameDAO.getAllGames();
        assertEquals(1,games.size());

        gameDAO.addGame("Game 2");
        games = gameDAO.getAllGames();
        assertEquals(2,games.size());

        gameDAO.addGame("Game 3");
        games = gameDAO.getAllGames();
        assertEquals(3,games.size());
    }

    @Test
    void addGameSameName() throws DataAccessException {
        GameData gameOne = gameDAO.addGame("Game");
        GameData gameTwo = gameDAO.addGame("Game");

        assertNotEquals(gameOne, gameTwo);
    }

    @Test
    void getGameError() throws DataAccessException {
        gameDAO.addGame("Game 1");
        assertNull(gameDAO.getGame(2));
    }

    @Test
    void updateGameError() throws DataAccessException {
        gameDAO.updateGame(new GameData(1,null,null,"No Game", new ChessGame()));
        assertNull(gameDAO.getGame(1));

        GameData newGame = gameDAO.addGame("New Game");
        assertNotNull(gameDAO.getGame(1));
        assertEquals("New Game", newGame.gameName());
    }

    @Test
    void getAllGamesError() throws DataAccessException {
        assertEquals(0,gameDAO.getAllGames().size());
    }
}