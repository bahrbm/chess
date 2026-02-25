package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import service.*;
import service.request.*;
import service.result.*;

import javax.xml.crypto.Data;

public class Server {

    private final Javalin javalin;
    private final UserService userService = new UserService(new MemoryUserDAO(),new MemoryAuthDAO());
    private final ClearService clearService = new ClearService(new MemoryUserDAO(), new MemoryAuthDAO(), new MemoryGameDAO());

    public Server() {


        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user",this::addUser)
                .delete("/db", this::clearDB)
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

    private void clearDB(Context ctx) throws DataAccessException{
        ClearRequest clearRequest = new Gson().fromJson(ctx.body(), ClearRequest.class);
        clearService.clear(clearRequest);
        //ctx.result(new Gson().toJson(clearResult));
    }

    private void exceptionHandler(DataAccessException ex, Context ctx){
        ctx.status(ex.toHttpStatusCode());
        ctx.result(ex.toJson());
    }


}
