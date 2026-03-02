package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {

    private GameDAO gameDAO = new MemoryGameDAO();
    private UserDAO userDAO = new MemoryUserDAO();
    private AuthDAO authDAO = new MemoryAuthDAO();
    private ClearService service = new ClearService(userDAO,authDAO,gameDAO);

    @Test
    void clear() {
        UserData user = new UserData("a","A","a@mail");
        AuthData auth = new AuthData("a","asdfghjk;lasdf");
        GameData game = new GameData(1,"white","black","best game",new ChessGame());

        userDAO.createUser(user);
        authDAO.createAuth(auth);
        gameDAO.addGame("best game");

        service.clear();
    }
}