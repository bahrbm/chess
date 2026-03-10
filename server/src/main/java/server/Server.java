package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import org.eclipse.jetty.server.Authentication;
import service.*;
import service.request.*;
import service.result.*;

public class Server {

    private final Javalin javalin;
    private final UserService userService;
    private final ClearService clearService;
    private final GameService gameService;

    public Server(){
        this(new UserService(new MemoryUserDAO(), new MemoryAuthDAO()),
             new GameService(new MemoryGameDAO()),
             new ClearService(new MemoryUserDAO(), new MemoryAuthDAO(), new MemoryGameDAO()));
    }

    public Server(UserService userService, GameService gameService, ClearService clearService) {
        this.userService  = userService;
        this.gameService  = gameService;
        this.clearService = clearService;

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::addUser)
                .delete("/db", this::clearDB)
                .post("/session", this::loginUser)
                .delete("/session", this::logoutUser)
                .post("/game", this::createGame)
                .put("/game", this::joinPlayer)
                .get("/game", this::listGames)
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

        userService.isAuthTokenValid(logoutRequest.authToken());

        userService.logout(logoutRequest);
    }

    private void createGame(Context ctx) throws DataAccessException{
        CreateGameRequest createGameRequest = new Gson().fromJson(ctx.body(), CreateGameRequest.class);
        String authToken = ctx.header("Authorization");

        userService.isAuthTokenValid(authToken);

        CreateGameResult createGameResult = gameService.create(createGameRequest);
        ctx.result(new Gson().toJson(createGameResult));
    }

    private void joinPlayer(Context ctx) throws DataAccessException{
        JoinGameRequest joinGameRequest = new Gson().fromJson(ctx.body(),JoinGameRequest.class);
        String authToken = ctx.header("Authorization");

        userService.isAuthTokenValid(authToken);

        gameService.joinGame(joinGameRequest,userService.getUser(authToken));
    }

    private void listGames(Context ctx) throws DataAccessException{
        String authToken = ctx.header("Authorization");

        userService.isAuthTokenValid(authToken);

        ListGamesResult result = gameService.listGames();

        ctx.result(new Gson().toJson(result));
    }

    private void clearDB(Context ctx) throws DataAccessException{
        clearService.clear();
    }

    private void exceptionHandler(DataAccessException ex, Context ctx){
        ctx.status(ex.toHttpStatusCode());
        ctx.result(ex.toJson());
    }


}
