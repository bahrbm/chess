package websocket;

import com.google.gson.Gson;
import dataaccess.SQLUserDAO;
import dataaccess.UserDAO;
import exception.DataAccessException;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    UserService userService;

    public WebSocketHandler(UserService us){
        this.userService = us;
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
            switch (action.getCommandType()) {
                case CONNECT -> enter(playerName, gameID, ctx.session);
                case LEAVE -> exit(playerName, gameID, ctx.session);
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

    private void enter(String visitorName, int gameID, Session session) throws IOException {
        connections.add(gameID,session);
        var message = String.format("%s joined the game", visitorName);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, session, notification);
    }

    private void exit(String visitorName, int gameID, Session session) throws IOException {
        var message = String.format("%s left the game", visitorName);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, session, notification);
        connections.remove(gameID, session);
    }
}