package service;

import dataaccess.*;
import model.*;
import service.request.*;
import service.result.*;
import java.util.Objects;
import java.util.UUID;


public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException{

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
            throw new DataAccessException(DataAccessException.ErrorCode.BadRequest,"Error: bad request");
        }

        UserData currUser = userDAO.getUser(username);

        if(!Objects.equals(currUser.password(), password)){
            throw new DataAccessException(DataAccessException.ErrorCode.Unauthorized,"Error: unauthorized");
        }

        String authToken = generateToken(username,authDAO);
        return new LoginResult(username,authToken);
    };

    public void logout(LogoutRequest logoutRequest){
        authDAO.deleteAuth(logoutRequest.authToken());
    };

    private boolean isUserInDatabase(String username){
        return userDAO.findByUsername(username) != null;
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

    public static String generateToken(String username, AuthDAO authDAO) {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(username, authToken);
        authDAO.createAuth(authData);
        return authToken;
    }

    public AuthData getUser(String authToken){
        return authDAO.findByAuthToken(authToken);
    }
}
