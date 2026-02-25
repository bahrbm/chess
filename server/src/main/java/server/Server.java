package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import service.*;
import service.request.*;
import service.result.*;

public class Server {

    private final Javalin javalin;
    private final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    private final MemoryUserDAO userDAO = new MemoryUserDAO();
    private final MemoryGameDAO gameDAO = new MemoryGameDAO();
    private final UserService userService = new UserService(userDAO, authDAO);
    private final ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);

    public Server() {


        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::addUser)
                .delete("/db", this::clearDB)
                .post("/session", this::loginUser)
                .delete("/session", this::logoutUser)
                .exception(DataAccessException.class, this::exceptionHandler)
                ;

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void addUser(Context ctx) throws DataAccessException{
        RegisterRequest registerRequest = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        RegisterResult registerResult = userService.register(registerRequest);
        ctx.result(new Gson().toJson(registerResult));
    }

    private void loginUser(Context ctx) throws DataAccessException{
        LoginRequest loginRequest = new Gson().fromJson(ctx.body(), LoginRequest.class);
        LoginResult loginResult = userService.login(loginRequest);
        ctx.result(new Gson().toJson(loginResult));
    }

    private void logoutUser(Context ctx) throws DataAccessException{
        LogoutRequest logoutRequest = new LogoutRequest(ctx.header("Authorization"));
        userService.logout(logoutRequest);
    }

    private void clearDB(Context ctx) throws DataAccessException{
        clearService.clear();
    }

    private void exceptionHandler(DataAccessException ex, Context ctx){
        ctx.status(ex.toHttpStatusCode());
        ctx.result(ex.toJson());
    }


}
