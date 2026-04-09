package websocket.commands;

import chess.*;

public class MakeMoveCommand extends UserGameCommand{
    private final ChessMove move;

    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID, ChessGame.TeamColor team, ChessMove move) {
        super(commandType, authToken, gameID, team);
        this.move = move;
    }

    public ChessMove getMove(){
        return move;
    }
}
