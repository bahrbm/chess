package chess;

import java.util.Collection;
import java.util.List;

public class MoveCalc {

    private ChessPosition position;
    private ChessBoard board;

    public MoveCalc(ChessBoard board, ChessPosition position) {
        this.position = position;
        this.board = board;
    }

    public Collection<ChessMove> findMoves(){
        return List.of();
    }
}
