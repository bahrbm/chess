package chess.Pieces;

import chess.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Knight implements MoveCalc{
    private final ChessBoard board;
    private final ChessPosition startPosition;
    private final Collection<ChessMove> validMoves = new LinkedList<>();

    public Knight(ChessBoard board, ChessPosition startPosition){
        this.board = board;
        this.startPosition = startPosition;
    }

    @Override
    public Collection<ChessMove> findMoves() {
        return List.of();
    }
}
