package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import exception.ResponseException;
import io.javalin.websocket.*;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.Session;
import request.MakeMoveRequest;
import service.GameService;
import service.UserService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;

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

            ChessGame.TeamColor team = cmd.getTeam();
            gameID = cmd.getGameID();
            String playerName = userService.getUser(cmd.getAuthToken()).username();
            ChessGame currGame = gameService.getGame(gameID);
            String whiteUser = gameService.getWhiteUser(gameID);
            String blackUser = gameService.getBlackUser(gameID);
            ChessGame.TeamColor currTurn = currGame.getTeamTurn();

            if(cmd.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE){
                cmd = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);

                if(currTurn == ChessGame.TeamColor.WHITE){
                    if(!Objects.equals(playerName, whiteUser)){
                        throw new ResponseException(ResponseException.Code.ClientError, "Error: Not your turn");
                    }
                }
                else{
                    if(!Objects.equals(playerName, blackUser)){
                        throw new ResponseException(ResponseException.Code.ClientError, "Error: Not your turn");
                    }
                }

                MakeMoveRequest r = new MakeMoveRequest(cmd.getGameID(),((MakeMoveCommand) cmd).getMove());
                gameService.makeMove(r);
            }

            currGame = gameService.getGame(gameID);

            switch (cmd.getCommandType()) {
                case CONNECT -> enter(playerName, gameID, currGame, session, team);
                case LEAVE -> exit(playerName, gameID, session, team);
                case MAKE_MOVE -> move(playerName, gameID, currGame, (MakeMoveCommand) cmd, session);
                case RESIGN -> announceResign(playerName, gameID, team);
            }
        } catch (Exception ex) {
            try{
                sendError(ex, ctx);
            }
            catch(IOException exception){
                exception.printStackTrace();
            }
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

        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        var update = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, currGame);
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

        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, session, notification);
        connections.remove(gameID, session);
    }

    private void move(String playerName, int gameID, ChessGame game, MakeMoveCommand cmd, Session session) throws IOException {

        ChessMove move = cmd.getMove();

        ChessPosition startPositon = move.getStartPosition();
        ChessPosition endPosition  = move.getEndPosition();

        String message = String.format("%s has made the move %s %d %s %d \n",playerName, translate(startPositon.getColumn()),
                startPositon.getRow(), translate(endPosition.getColumn()), endPosition.getRow());

        String gameState = "";

        if(game.isInCheckmate(ChessGame.TeamColor.WHITE)){
            gameState = "White is in Checkmate. Black has won and the game is over";
        }
        else if(game.isInCheckmate(ChessGame.TeamColor.BLACK)){
            gameState = "Black is in Checkmate. White has won and the game is over";
        }
        else if(game.isInStalemate(ChessGame.TeamColor.WHITE) || game.isInStalemate(ChessGame.TeamColor.BLACK)){
            gameState = "Stalemate. The game is over";
        }
        else if(game.isInCheck(ChessGame.TeamColor.WHITE)){
            gameState = "White is now in check";
        }
        else if(game.isInCheck(ChessGame.TeamColor.BLACK)){
            gameState = "Black is now in check";
        }

        var update = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        var gameStateNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, gameState);
        connections.reloadAllClients(gameID,update);
        connections.broadcast(gameID, session, notification);

        if(game.isInCheckmate(ChessGame.TeamColor.BLACK) || game.isInCheckmate(ChessGame.TeamColor.WHITE)){
            connections.announce(gameID,gameStateNotification);
        }
    }

    private void announceResign(String playerName, int gameID, ChessGame.TeamColor team) throws IOException {

        String message = "";

        if(team == ChessGame.TeamColor.WHITE){
            message = String.format("%s has resigned. Black wins", playerName);
        }
        else if(team == ChessGame.TeamColor.BLACK){
            message = String.format("%s has resigned. White wins", playerName);
        }

        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.announce(gameID, notification);
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

    private void sendError(Exception ex, WsMessageContext ctx) throws IOException {
        var action = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        String message = "Error: " + ex.getMessage();
        var notification = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
        connections.sendErrorMessage(action.getGameID(), ctx.session, notification);
    }
}