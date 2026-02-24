package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import service.request.*;
import service.result.*;


public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO){
        this.userDAO = userDAO;
    }
    public RegisterResult register(RegisterRequest registerRequest){};
    //public LoginResult login(LoginRequest loginRequest){};
    //public void logout(LogoutRequest logoutRequest){};
}
