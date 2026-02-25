package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import service.UserService;
import service.request.*;
import service.result.*;

import javax.xml.crypto.Data;

public class Server {

    private final Javalin javalin;
    private final UserService userService = new UserService(new MemoryUserDAO(),new MemoryAuthDAO());

    public Server() {


        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user",this::addUser)
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

    private void exceptionHandler(DataAccessException ex, Context ctx){
        ctx.status(ex.toHttpStatusCode());
        ctx.result(ex.toJson());
    }


}
