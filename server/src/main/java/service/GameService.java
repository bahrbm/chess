package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import service.result.*;
import service.request.*;

import java.util.Objects;

public class GameService {

    private final GameDAO gameDAO;

    public GameService(GameDAO gameDAO){
        this.gameDAO = gameDAO;
    }

    public CreateGameResult create(CreateGameRequest r) throws DataAccessException {
        String gameName = r.gameName();

        if(gameName == null){
            throw new DataAccessException(DataAccessException.ErrorCode.BadRequest,"Error: bad request");
        }

        int gameID = gameDAO.addGame(gameName);
        return new CreateGameResult(gameID);
    }

    public void joinGame(JoinGameRequest r, AuthData a) throws DataAccessException{

        GameData currGame = gameDAO.getGame(r.gameID());

        if(r.playerColor()==null){
            throw new DataAccessException(DataAccessException.ErrorCode.BadRequest,"Error: bad request");
        }

        if(currGame == null){
            throw new DataAccessException(DataAccessException.ErrorCode.BadRequest,"Error: bad request");
        }

        if(Objects.equals(r.playerColor(), "BLACK")){

            if(currGame.blackUsername() != null){
                throw new DataAccessException(DataAccessException.ErrorCode.AlreadyTaken,"Error: team taken");
            }

            GameData newGame = new GameData(currGame.gameID(),currGame.whiteUsername(),a.username(),currGame.gameName(), new ChessGame());
            gameDAO.updateGame(newGame);
        }
        else{

            if(currGame.whiteUsername() != null){
                throw new DataAccessException(DataAccessException.ErrorCode.AlreadyTaken,"Error: team taken");
            }

            GameData newGame = new GameData(currGame.gameID(),a.username(),currGame.blackUsername(), currGame.gameName(), new ChessGame());
            gameDAO.updateGame(newGame);
        }
    }
}
