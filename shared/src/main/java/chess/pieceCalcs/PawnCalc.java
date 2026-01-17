package chess.pieceCalcs;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import java.util.Collection;

public class PawnCalc {

    private ChessPosition startPosition;
    private ChessBoard board;

    public PawnCalc(ChessPosition startPosition, ChessBoard board) {
        this.startPosition = startPosition;
        this.board = board;
    }

//    public Collection<ChessMove> findMoves(){
//    }
}
