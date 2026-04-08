package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import service.GameService;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    UserService userService;
    GameService gameService;

    public WebSocketHandler(UserService us, GameService gs){
        userService = us;
        gameService = gs;
    }

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        int gameID = -1;
        Session session = ctx.session;

        try {
            UserGameCommand action = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            ChessGame.TeamColor team = action.getTeam();
            gameID = action.getGameID();
            String playerName = userService.getUser(action.getAuthToken()).username();
            ChessGame currGame = gameService.getGame(gameID);
            switch (action.getCommandType()) {
                case CONNECT -> enter(playerName, gameID, currGame, session, team);
                case LEAVE -> exit(playerName, gameID, session, team);
//                case MAKE_MOVE ->;
//                case RESIGN -> ;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void enter(String visitorName, int gameID, ChessGame currGame, Session session, ChessGame.TeamColor team) throws IOException {
        connections.add(gameID,session);

        String message = "";

        if(team == ChessGame.TeamColor.WHITE){
            message = String.format("%s joined the white team", visitorName);
        }
        else if(team == ChessGame.TeamColor.BLACK){
            message = String.format("%s joined the black team", visitorName);
        }
        else{
            message = String.format("%s joined the game", visitorName);
        }

        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        var update = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, message, currGame);
        connections.userHasJoined(gameID, session, notification, update);
    }

    private void exit(String visitorName, int gameID, Session session, ChessGame.TeamColor team) throws IOException {

        String message = "";

        if(team == ChessGame.TeamColor.WHITE){
            message = String.format("%s left the white team", visitorName);
        }
        else if(team == ChessGame.TeamColor.BLACK){
            message = String.format("%s left the black team", visitorName);
        }
        else{
            message = String.format("%s left the game", visitorName);
        }

        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, session, notification);
        connections.remove(gameID, session);
    }
}