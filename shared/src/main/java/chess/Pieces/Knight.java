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
        // Get the piece that we are looking at, as well as the row and column that the piece is on
        ChessPiece myPiece = board.getPiece(startPosition);
        int row = startPosition.getRow();
        int col = startPosition.getColumn();

        // Create Position objects
        if(row+2 > 0 && row+2 < 9 && col+1 > 0 && col+1 < 9){
            ChessPosition currPos = new ChessPosition(row+2,col+1);
            if(board.getPiece(currPos)==null || board.getPiece(currPos).getTeamColor() != myPiece.getTeamColor()){
                validMoves.add(new ChessMove(startPosition,currPos,null));
            }
        }
        if(row+1 > 0 && row+1 < 9 && col+2 > 0 && col+2 < 9){
            ChessPosition currPos = new ChessPosition(row+1,col+2);
            if(board.getPiece(currPos)==null || board.getPiece(currPos).getTeamColor() != myPiece.getTeamColor()){
                validMoves.add(new ChessMove(startPosition,currPos,null));
            }
        }
        if(row-1 > 0 && row-1 < 9 && col+2 > 0 && col+2 < 9){
            ChessPosition currPos = new ChessPosition(row-1,col+2);
            if(board.getPiece(currPos)==null || board.getPiece(currPos).getTeamColor() != myPiece.getTeamColor()){
                validMoves.add(new ChessMove(startPosition,currPos,null));
            }
        }
        if(row-2 > 0 && row-2 < 9 && col+1 > 0 && col+1 < 9){
            ChessPosition currPos = new ChessPosition(row-2,col+1);
            if(board.getPiece(currPos)==null || board.getPiece(currPos).getTeamColor() != myPiece.getTeamColor()){
                validMoves.add(new ChessMove(startPosition,currPos,null));
            }
        }
        if(row-2 > 0 && row-2 < 9 && col-1 > 0 && col-1 < 9){
            ChessPosition currPos = new ChessPosition(row-2,col-1);
            if(board.getPiece(currPos)==null || board.getPiece(currPos).getTeamColor() != myPiece.getTeamColor()){
                validMoves.add(new ChessMove(startPosition,currPos,null));
            }
        }
        if(row-1 > 0 && row-1 < 9 && col-2 > 0 && col-2 < 9){
            ChessPosition currPos = new ChessPosition(row-1,col-2);
            if(board.getPiece(currPos)==null || board.getPiece(currPos).getTeamColor() != myPiece.getTeamColor()){
                validMoves.add(new ChessMove(startPosition,currPos,null));
            }
        }
        if(row+1 > 0 && row+1 < 9 && col-2 > 0 && col-2 < 9){
            ChessPosition currPos = new ChessPosition(row+1,col-2);
            if(board.getPiece(currPos)==null || board.getPiece(currPos).getTeamColor() != myPiece.getTeamColor()){
                validMoves.add(new ChessMove(startPosition,currPos,null));
            }
        }
        if(row+2 > 0 && row+2 < 9 && col-1 > 0 && col-1 < 9){
            ChessPosition currPos = new ChessPosition(row+2,col-1);
            if(board.getPiece(currPos)==null || board.getPiece(currPos).getTeamColor() != myPiece.getTeamColor()){
                validMoves.add(new ChessMove(startPosition,currPos,null));
            }
        }
        return validMoves;
    }
}
