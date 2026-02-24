package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import service.UserService;
import service.request.*;
import service.result.*;

public class Server {

    private final Javalin javalin;
    private final UserService userService = new UserService(new MemoryUserDAO());

    public Server() {


        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user",this::addUser);


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
}
