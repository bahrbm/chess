package chess.pieces;

import chess.*;
import java.util.Collection;
import java.util.LinkedList;

public class Pawn implements MoveCalc{
    private final ChessBoard board;
    private final ChessPosition startPosition;
    private final Collection<ChessMove> validMoves = new LinkedList<>();
    private final ChessGame.TeamColor myTeam;

    public Pawn(ChessBoard board, ChessPosition startPosition){
        this.board = board;
        this.startPosition = startPosition;
        myTeam = board.getPiece(startPosition).getTeamColor();
    }

    @Override
    public Collection<ChessMove> findMoves() {
        // Get row and column
        int row = startPosition.getRow();
        int col = startPosition.getColumn();

        // White Moves
        if(myTeam == ChessGame.TeamColor.WHITE){
            checkWhiteCapture(row,col);
        }
        else{
            checkBlackCapture(row,col);
        }

        Forward(row,col);

        return validMoves;
    }

    void Forward(int row, int col){

        ChessPosition[] forwardMoves = findForwardMoves(row,col);

        // Check if there is a piece directly in front of the pawn
        if(board.getPiece(forwardMoves[0])!=null){
            return;
        }

        // Check for starting moves
        if(isAtStart(row)){
            validMoves.add(new ChessMove(startPosition,forwardMoves[0],null));
            if(board.getPiece(forwardMoves[1])==null){
                validMoves.add(new ChessMove(startPosition,forwardMoves[1],null));
            }
        }
        // Check for promotion
        else if(canPromote(row)){
            addPromotion(forwardMoves[0]);
        }
        else{
            validMoves.add(new ChessMove(startPosition,forwardMoves[0],null));
        }
    }

    void checkWhiteCapture(int row, int col){

        // Check right diagonal
        if(col+1 < 9){
            // Create new position
            ChessPosition rightDiagonal = new ChessPosition(row+1,col+1);

            // Check if there is a piece in that position
            if(board.getPiece(rightDiagonal)!=null){
                // Check if the piece is on the other team
                if(board.getPiece(rightDiagonal).getTeamColor() == ChessGame.TeamColor.BLACK){
                    // Check for promotion
                    if(canPromote(row)){
                        addPromotion(rightDiagonal);
                    }
                    else{
                        validMoves.add(new ChessMove(startPosition,rightDiagonal,null));
                    }
                }
            }
        }

        // Check left diagonal
        if(col-1 > 0){
            // Create new position
            ChessPosition leftDiagonal = new ChessPosition(row+1,col-1);

            // Check if there is a piece in that position
            if(board.getPiece(leftDiagonal)!=null){
                // Check if the piece is on the other team
                if(board.getPiece(leftDiagonal).getTeamColor() == ChessGame.TeamColor.BLACK){
                    // Check for promotion
                    if(canPromote(row)){
                        addPromotion(leftDiagonal);
                    }
                    else{
                        validMoves.add(new ChessMove(startPosition,leftDiagonal,null));
                    }
                }
            }
        }
    }

    void checkBlackCapture(int row, int col){

        // Check right diagonal
        if(col+1 < 9){
            // Create new position
            ChessPosition rightDiagonal = new ChessPosition(row-1,col+1);

            // Check if there is a piece in that position
            if(board.getPiece(rightDiagonal)!=null){
                // Check if the piece is on the other team
                if(board.getPiece(rightDiagonal).getTeamColor() == ChessGame.TeamColor.WHITE){
                    // Check for promotion
                    if(canPromote(row)){
                        addPromotion(rightDiagonal);
                    }
                    else{
                        validMoves.add(new ChessMove(startPosition,rightDiagonal,null));
                    }
                }
            }
        }

        // Check left diagonal
        if(col-1 > 0){
            // Create new position
            ChessPosition leftDiagonal = new ChessPosition(row-1,col-1);

            // Check if there is a piece in that position
            if(board.getPiece(leftDiagonal)!=null){
                // Check if the piece is on the other team
                if(board.getPiece(leftDiagonal).getTeamColor() == ChessGame.TeamColor.WHITE){
                    // Check for promotion
                    if(canPromote(row)){
                        addPromotion(leftDiagonal);
                    }
                    else{
                        validMoves.add(new ChessMove(startPosition,leftDiagonal,null));
                    }
                }
            }
        }
    }

    boolean canPromote(int row){

        if(myTeam == ChessGame.TeamColor.WHITE && row == 7){
            return true;
        }
        else if (myTeam == ChessGame.TeamColor.BLACK && row == 2){
            return true;
        }

        return false;
    }

    boolean isAtStart(int row){
        if(myTeam == ChessGame.TeamColor.WHITE && row == 2){
            return true;
        }
        else if (myTeam == ChessGame.TeamColor.BLACK && row == 7){
            return true;
        }

        return false;
    }

    void addPromotion(ChessPosition endPosition){
        validMoves.add(new ChessMove(startPosition,endPosition, ChessPiece.PieceType.QUEEN));
        validMoves.add(new ChessMove(startPosition,endPosition, ChessPiece.PieceType.BISHOP));
        validMoves.add(new ChessMove(startPosition,endPosition, ChessPiece.PieceType.ROOK));
        validMoves.add(new ChessMove(startPosition,endPosition, ChessPiece.PieceType.KNIGHT));
    }

    ChessPosition[] findForwardMoves(int row, int col){
        if(myTeam == ChessGame.TeamColor.BLACK){
            return new ChessPosition[]{new ChessPosition(row-1,col),
                                       new ChessPosition(row-2,col)};
        }
        else{
            return new ChessPosition[]{new ChessPosition(row+1,col),
                                       new ChessPosition(row+2,col)};
        }
    }
}