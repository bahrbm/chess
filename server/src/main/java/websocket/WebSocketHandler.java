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
            gameID = action.getGameID();
            String playerName = userService.getUser(action.getAuthToken()).username();
            ChessGame currGame = gameService.getGame(gameID);
            switch (action.getCommandType()) {
                case CONNECT -> enter(playerName, gameID, currGame, session);
                case LEAVE -> exit(playerName, gameID, session);
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

    private void enter(String visitorName, int gameID, ChessGame currGame, Session session) throws IOException {
        connections.add(gameID,session);

        var message = String.format("%s joined the game", visitorName);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        var update = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, message, currGame);
        connections.userHasJoined(gameID, session, notification, update);
    }

    private void exit(String visitorName, int gameID, Session session) throws IOException {
        var message = String.format("%s left the game", visitorName);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, session, notification);
        connections.remove(gameID, session);
    }
}