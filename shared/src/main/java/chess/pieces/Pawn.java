package chess.pieces;

import chess.*;
import java.util.Collection;
import java.util.LinkedList;

public class Pawn implements MoveCalc{
    private final ChessBoard board;
    private final ChessPosition startPosition;
    private final Collection<ChessMove> validMoves = new LinkedList<>();

    public Pawn(ChessBoard board, ChessPosition startPosition){
        this.board = board;
        this.startPosition = startPosition;
    }

    @Override
    public Collection<ChessMove> findMoves() {
        // Get the piece, row, column, and team color
        ChessPiece myPiece = board.getPiece(startPosition);
        int row = startPosition.getRow();
        int col = startPosition.getColumn();
        ChessGame.TeamColor myTeam = myPiece.getTeamColor();

        // White Moves
        if(myTeam == ChessGame.TeamColor.WHITE){
            whiteForward(row,col);
            checkWhiteCapture(row,col);
        }
        else{
            blackForward(row,col);
            checkBlackCapture(row,col);
        }

        return validMoves;
    }

    void whiteForward(int row, int col){

        ChessPosition forwardOne = new ChessPosition(row+1,col);
        ChessPosition forwardTwo = new ChessPosition(row+2,col);

        // Check if there is a piece directly in front of the pawn
        if(board.getPiece(forwardOne)!=null){
            return;
        }

        // Check for starting moves
        if(row==2){
            validMoves.add(new ChessMove(startPosition,forwardOne,null));
            if(board.getPiece(forwardTwo)==null){
                validMoves.add(new ChessMove(startPosition,forwardTwo,null));
            }
        }
        // Check for promotion
        else if(row==7){
            validMoves.add(new ChessMove(startPosition,forwardOne, ChessPiece.PieceType.QUEEN));
            validMoves.add(new ChessMove(startPosition,forwardOne, ChessPiece.PieceType.BISHOP));
            validMoves.add(new ChessMove(startPosition,forwardOne, ChessPiece.PieceType.ROOK));
            validMoves.add(new ChessMove(startPosition,forwardOne, ChessPiece.PieceType.KNIGHT));
        }
        else{
            validMoves.add(new ChessMove(startPosition,forwardOne,null));
        }
    }

    void blackForward(int row, int col){

        ChessPosition forwardOne = new ChessPosition(row-1,col);
        ChessPosition forwardTwo = new ChessPosition(row-2,col);

        // Check if there is a piece directly in front of the pawn
        if(board.getPiece(forwardOne)!=null){
            return;
        }

        // Check for starting moves
        if(row==7){
            validMoves.add(new ChessMove(startPosition,forwardOne,null));
            if(board.getPiece(forwardTwo)==null){
                validMoves.add(new ChessMove(startPosition,forwardTwo,null));
            }
        }
        // Check for promotion
        else if(row==2){
            validMoves.add(new ChessMove(startPosition,forwardOne, ChessPiece.PieceType.QUEEN));
            validMoves.add(new ChessMove(startPosition,forwardOne, ChessPiece.PieceType.BISHOP));
            validMoves.add(new ChessMove(startPosition,forwardOne, ChessPiece.PieceType.ROOK));
            validMoves.add(new ChessMove(startPosition,forwardOne, ChessPiece.PieceType.KNIGHT));
        }
        else{
            validMoves.add(new ChessMove(startPosition,forwardOne,null));
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
                    if(row==7){
                        validMoves.add(new ChessMove(startPosition,rightDiagonal, ChessPiece.PieceType.QUEEN));
                        validMoves.add(new ChessMove(startPosition,rightDiagonal, ChessPiece.PieceType.BISHOP));
                        validMoves.add(new ChessMove(startPosition,rightDiagonal, ChessPiece.PieceType.ROOK));
                        validMoves.add(new ChessMove(startPosition,rightDiagonal, ChessPiece.PieceType.KNIGHT));
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
                    if(row==7){
                        validMoves.add(new ChessMove(startPosition,leftDiagonal, ChessPiece.PieceType.QUEEN));
                        validMoves.add(new ChessMove(startPosition,leftDiagonal, ChessPiece.PieceType.BISHOP));
                        validMoves.add(new ChessMove(startPosition,leftDiagonal, ChessPiece.PieceType.ROOK));
                        validMoves.add(new ChessMove(startPosition,leftDiagonal, ChessPiece.PieceType.KNIGHT));
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
                    if(row==2){
                        validMoves.add(new ChessMove(startPosition,rightDiagonal, ChessPiece.PieceType.QUEEN));
                        validMoves.add(new ChessMove(startPosition,rightDiagonal, ChessPiece.PieceType.BISHOP));
                        validMoves.add(new ChessMove(startPosition,rightDiagonal, ChessPiece.PieceType.ROOK));
                        validMoves.add(new ChessMove(startPosition,rightDiagonal, ChessPiece.PieceType.KNIGHT));
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
                    if(row==2){
                        validMoves.add(new ChessMove(startPosition,leftDiagonal, ChessPiece.PieceType.QUEEN));
                        validMoves.add(new ChessMove(startPosition,leftDiagonal, ChessPiece.PieceType.BISHOP));
                        validMoves.add(new ChessMove(startPosition,leftDiagonal, ChessPiece.PieceType.ROOK));
                        validMoves.add(new ChessMove(startPosition,leftDiagonal, ChessPiece.PieceType.KNIGHT));
                    }
                    else{
                        validMoves.add(new ChessMove(startPosition,leftDiagonal,null));
                    }
                }
            }
        }
    }

}