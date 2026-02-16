package chess.pieces;

import chess.*;
import java.util.Collection;
import java.util.LinkedList;

public class Queen implements MoveCalc{
    private final ChessBoard board;
    private final ChessPosition startPosition;
    private final Collection<ChessMove> validMoves = new LinkedList<>();

    public Queen(ChessBoard board, ChessPosition startPosition){
        this.board = board;
        this.startPosition = startPosition;
    }

    @Override
    public Collection<ChessMove> findMoves() {
        // Get the current piece so we can get the color, and get the adjusted row and column for indexing
        MoveCalc fakeBishop = new Bishop(board, startPosition);
        MoveCalc fakeRook = new Rook(board,startPosition);
        validMoves.addAll(fakeBishop.findMoves());
        validMoves.addAll(fakeRook.findMoves());
        return validMoves;
    }
}