package service;

import dataaccess.*;
import exception.DataAccessException;
import model.*;
import org.mindrot.jbcrypt.BCrypt;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

import java.util.UUID;


public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {

        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();

        if(username == null || password == null || email == null){
            throw new DataAccessException(DataAccessException.ErrorCode.BadRequest,"Error: bad request");
        }

        if(isUserInDatabase(username)){
            throw new DataAccessException(DataAccessException.ErrorCode.AlreadyTaken,"Error: already taken");
        }

        UserData newUser = new UserData(username,password,email);
        userDAO.createUser(newUser);

        String authToken = generateToken(username, authDAO);
        return new RegisterResult(username,authToken);
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException{

        String username = loginRequest.username();
        String password = loginRequest.password();

        if(username == null || password == null){
            throw new DataAccessException(DataAccessException.ErrorCode.BadRequest,"Error: bad request");
        }
        else if(!isUserInDatabase(username)){
            throw new DataAccessException(DataAccessException.ErrorCode.Unauthorized,"Error: unauthorized");
        }

        UserData currUser = userDAO.findByUsername(username);
        var hashedPassword = currUser.password();

        if(!BCrypt.checkpw(password, hashedPassword)){
            throw new DataAccessException(DataAccessException.ErrorCode.Unauthorized,"Error: unauthorized");
        }

        String authToken = generateToken(username,authDAO);
        return new LoginResult(username,authToken);
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        authDAO.deleteAuth(logoutRequest.authToken());
    }

    public void isAuthTokenValid(String authToken) throws DataAccessException{

        if(authToken == null){
            throw new DataAccessException(DataAccessException.ErrorCode.BadRequest,"Error: bad request");
        }

        AuthData authData = authDAO.findByAuthToken(authToken);

        if(authData == null){
            throw new DataAccessException(DataAccessException.ErrorCode.Unauthorized,"Error: unauthorized");
        }
    }

    public AuthData getUser(String authToken) throws DataAccessException {
        AuthData data = authDAO.findByAuthToken(authToken);

        if(data==null){
            throw new DataAccessException(DataAccessException.ErrorCode.Unauthorized,"Error: unauthorized");
        }

        return data;
    }

    private static String generateToken(String username, AuthDAO authDAO) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(username, authToken);
        authDAO.createAuth(authData);
        return authToken;
    }

    private boolean isUserInDatabase(String username) throws DataAccessException {
        return userDAO.findByUsername(username) != null;
    }
}
