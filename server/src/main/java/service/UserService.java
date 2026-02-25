package service;

import dataaccess.*;
import model.*;
import service.request.*;
import service.result.*;

import javax.xml.crypto.Data;
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

        if(isUserInDatabase(username)){
            throw new DataAccessException(DataAccessException.ErrorCode.AlreadyTaken,"Error: already taken");
        }

        UserData newUser = new UserData(username,password,email);
        userDAO.createUser(newUser);

        String authToken = generateToken();
        AuthData authData = new AuthData(authToken,username);
        authDAO.createAuth(authData);

        return new RegisterResult(username,authToken);
    }

    //public LoginResult login(LoginRequest loginRequest){};
    //public void logout(LogoutRequest logoutRequest){};

    private boolean isUserInDatabase(String username){
        return userDAO.findByUsername(username) != null;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
