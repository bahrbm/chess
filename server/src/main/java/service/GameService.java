package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.*;
import exception.DataAccessException;
import model.*;
import request.*;
import result.*;
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

        GameData newGame = gameDAO.addGame(gameName);
        return new CreateGameResult(newGame.gameID());
    }

    public void joinGame(JoinGameRequest r, AuthData a) throws DataAccessException{

        GameData currGame = gameDAO.getGame(r.gameID());

        if(r.playerColor()==null){
            System.out.println("Player Color not set properly");
            throw new DataAccessException(DataAccessException.ErrorCode.BadRequest,"Error: No player color specified");
        }

        if(currGame == null){
            System.out.println("Game is null?");
            throw new DataAccessException(DataAccessException.ErrorCode.BadRequest,"Error: bad request");
        }

        if(Objects.equals(r.playerColor(), "BLACK") || Objects.equals(r.playerColor(), "black")){

            if(currGame.blackUsername() != null && !Objects.equals(a.username(), currGame.blackUsername())){
                throw new DataAccessException(DataAccessException.ErrorCode.AlreadyTaken,"Error: team taken");
            }

            GameData newGame = new GameData(currGame.gameID(),currGame.whiteUsername(),a.username(),currGame.gameName(), currGame.game());
            gameDAO.updateGame(newGame);
        }
        else if(Objects.equals(r.playerColor(), "WHITE") || Objects.equals(r.playerColor(), "white")){

            if(currGame.whiteUsername() != null && !Objects.equals(a.username(), currGame.whiteUsername())){
                throw new DataAccessException(DataAccessException.ErrorCode.AlreadyTaken,"Error: team taken");
            }

            GameData newGame = new GameData(currGame.gameID(),a.username(),currGame.blackUsername(), currGame.gameName(), currGame.game());
            gameDAO.updateGame(newGame);
        }
        else{
            System.out.println("Invalid color");
            throw new DataAccessException(DataAccessException.ErrorCode.BadRequest,"Error: not a valid team color");
        }
    }

    public ListGamesResult listGames() throws DataAccessException {
        return new ListGamesResult(gameDAO.getAllGames());
    }

    public void leaveGame(LeaveGameRequest r, AuthData a) throws DataAccessException {

        int gameID           = r.gameID();
        GameData currGame    = gameDAO.getGame(gameID);
        String whiteUsername = currGame.whiteUsername();
        String blackUsername = currGame.blackUsername();
        String gameName      = currGame.gameName();
        ChessGame game       = currGame.game();

        if(Objects.equals(a.username(), currGame.whiteUsername())){
            GameData newGame = new GameData(gameID, null, blackUsername, gameName, game);
            gameDAO.updateGame(newGame);
        }
        else if(Objects.equals(a.username(), currGame.blackUsername())){
            GameData newGame = new GameData(gameID, whiteUsername, null, gameName, game);
            gameDAO.updateGame(newGame);
        }
        else{
            System.out.println("Access Denied");
            throw new DataAccessException(DataAccessException.ErrorCode.BadRequest,"Error: Access Denied");
        }
    }

    public void makeMove(MakeMoveRequest r) throws DataAccessException, InvalidMoveException {
        int gameID           = r.gameID();
        GameData currGame    = gameDAO.getGame(gameID);
        String whiteUsername = currGame.whiteUsername();
        String blackUsername = currGame.blackUsername();
        String gameName      = currGame.gameName();
        ChessGame game       = currGame.game();
        
        game.makeMove(r.requestedMove());
        GameData newGame = new GameData(gameID,whiteUsername,blackUsername,gameName,game);
        gameDAO.updateGame(newGame);
    }

    public ChessGame getGame(int gameID) throws DataAccessException {
        return gameDAO.getGame(gameID).game();
    }

    public String getWhiteUser(int gameID) throws DataAccessException {
        GameData game = gameDAO.getGame(gameID);
        return game.whiteUsername();
    }

    public String getBlackUser(int gameID) throws DataAccessException {
        GameData game = gameDAO.getGame(gameID);
        return game.blackUsername();
    }
}
