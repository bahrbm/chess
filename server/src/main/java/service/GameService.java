package service;

import dataaccess.GameDAO;
import service.result.*;
import service.request.*;

public class GameService {

    private final GameDAO gameDAO;

    public GameService(GameDAO gameDAO){
        this.gameDAO = gameDAO;
    }

    public CreateGameResult create(CreateGameRequest r){

    }
}
