package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.Session;
import request.MakeMoveRequest;
import service.GameService;
import service.UserService;
import websocket.commands.MakeMoveCommand;
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
            UserGameCommand cmd = new Gson().fromJson(ctx.message(), UserGameCommand.class);

            if(cmd.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE){
                cmd = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
            }

            ChessGame.TeamColor team = cmd.getTeam();
            gameID = cmd.getGameID();
            String playerName = userService.getUser(cmd.getAuthToken()).username();
            ChessGame currGame = gameService.getGame(gameID);

            switch (cmd.getCommandType()) {
                case CONNECT -> enter(playerName, gameID, currGame, session, team);
                case LEAVE -> exit(playerName, gameID, session, team);
                case MAKE_MOVE -> move(playerName, gameID, currGame, (MakeMoveCommand) cmd);
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
        var update = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, "", currGame);
        connections.userHasJoined(gameID, session, notification, update);
    }

    private void exit(String playerName, int gameID, Session session, ChessGame.TeamColor team) throws IOException {

        String message = "";

        if(team == ChessGame.TeamColor.WHITE){
            message = String.format("%s left the white team", playerName);
        }
        else if(team == ChessGame.TeamColor.BLACK){
            message = String.format("%s left the black team", playerName);
        }
        else{
            message = String.format("%s left the game", playerName);
        }

        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, session, notification);
        connections.remove(gameID, session);
    }

    private void move(String playerName, int gameID, ChessGame game, MakeMoveCommand cmd) throws IOException {

        ChessMove move = cmd.getMove();

        ChessPosition startPositon = move.getStartPosition();
        ChessPosition endPosition  = move.getEndPosition();

        String message = String.format("%s has made the move %d %s %d %s",playerName, startPositon.getRow(),
                                        translate(startPositon.getColumn()), endPosition.getRow(),
                                        translate(startPositon.getColumn()));

        var update = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, message, game);
        connections.reloadAllClients(gameID,update);
    }

    private String translate(int col){
        return switch(col){
            case 1 -> "a";
            case 2 -> "b";
            case 3 -> "c";
            case 4 -> "d";
            case 5 -> "e";
            case 6 -> "f";
            case 7 -> "g";
            case 8 -> "h";
            default -> throw new IllegalStateException("Unexpected value: " + col);
        };
    }
}