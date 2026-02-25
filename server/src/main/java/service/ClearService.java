package service;

import dataaccess.*;
import service.request.ClearRequest;
import service.result.ClearResult;

import javax.xml.crypto.Data;

public class ClearService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ClearService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void clear(ClearRequest clearRequest) throws DataAccessException{
        userDAO.clearUserData();
        authDAO.clearAuthData();
        gameDAO.clearGameData();
    }

}
