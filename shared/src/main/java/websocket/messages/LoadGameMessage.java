package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage{
    private ChessGame currGame;

    public LoadGameMessage(ServerMessageType type, String message, ChessGame game) {
        super(type, message);
        currGame = game;
    }

    public ChessGame getGame() {
        return currGame;
    }
}
