package request;

import chess.ChessMove;

public record MakeMoveRequest(int gameID, ChessMove requestedMove) {
}
