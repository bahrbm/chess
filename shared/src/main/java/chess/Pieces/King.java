package chess.Pieces;

import chess.*;

import java.util.Collection;
import java.util.LinkedList;

public class King implements MoveCalc{
    private final ChessPosition startPosition;
    private final ChessBoard board;
    private Collection<ChessMove> validMoves = new LinkedList<>();

    public King( ChessBoard board, ChessPosition startPosition) {
        this.startPosition = startPosition;
        this.board = board;
    }

    @Override
    public Collection<ChessMove> findMoves() {
        // The collection of Chess move objects which are composed of chess position objects (start, end, promo)

        // Get the current piece so we can get the color, and get the adjusted row and column for indexing
        ChessPiece myPiece = board.getPiece(startPosition);
        int row = startPosition.getRow();
        int col = startPosition.getColumn();

        // Check the surrounding positions and add all positions unless the same color piece exists
        for(int i = -1; i < 2; i++){
            for(int j = -1; j < 2; j++){

                // Skip the position if it is outside the board
                if (row+i < 1 || row+i > 8 || col+j < 1 || col+j > 8){
                    continue;
                }

                // Make a new object for the position we are considering
                ChessPosition currPos = new ChessPosition(row+i,col+j);

                // If there is no piece in this position, add it to the collection
                if(board.getPiece(currPos) == null){
                    validMoves.add(new ChessMove(startPosition,currPos,null));
                    continue;
                }

                // Add the move if the king can take the piece
                if(myPiece.getTeamColor() != board.getPiece(currPos).getTeamColor()){
                    validMoves.add(new ChessMove(startPosition,currPos,null));
                }
            }


        }

        return validMoves;
    }
}
