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
        // Get the piece as well as the row and column
        ChessPiece myPiece = board.getPiece(startPosition);
        int row = startPosition.getRow();
        int col = startPosition.getColumn();

        // First we need to see if the piece is white or black
        if(myPiece.getTeamColor() == ChessGame.TeamColor.WHITE){

            // Check if the pawn can go forward one, or capture a diagonal piece
            if(col+1 < 9){
                // diagonal right
                ChessPosition currPos = new ChessPosition(row+1,col+1);
                // Add the move if you can capture
                if(board.getPiece(currPos)!=null){
                    // First check if there is a piece there, then check the color
                    if(board.getPiece(currPos).getTeamColor() != myPiece.getTeamColor() && row+1 != 8){
                        validMoves.add(new ChessMove(startPosition,currPos,null));
                    }
                    else if(board.getPiece(currPos).getTeamColor() != myPiece.getTeamColor() && row+1 == 8){
                        validMoves.add(new ChessMove(startPosition,currPos,ChessPiece.PieceType.ROOK));
                        validMoves.add(new ChessMove(startPosition,currPos,ChessPiece.PieceType.KNIGHT));
                        validMoves.add(new ChessMove(startPosition,currPos,ChessPiece.PieceType.QUEEN));
                        validMoves.add(new ChessMove(startPosition,currPos,ChessPiece.PieceType.BISHOP));
                    }
                }
            }
            if(col-1 > 0){
                // diagonal left
                ChessPosition currPos = new ChessPosition(row+1,col-1);
                // Add the move if you can capture
                if(board.getPiece(currPos)!=null){
                    if(board.getPiece(currPos).getTeamColor() != myPiece.getTeamColor() && row+1 != 8){
                        validMoves.add(new ChessMove(startPosition,currPos,null));
                    }
                    else if(board.getPiece(currPos).getTeamColor() != myPiece.getTeamColor() && row+1 == 8){
                        validMoves.add(new ChessMove(startPosition,currPos,ChessPiece.PieceType.ROOK));
                        validMoves.add(new ChessMove(startPosition,currPos,ChessPiece.PieceType.KNIGHT));
                        validMoves.add(new ChessMove(startPosition,currPos,ChessPiece.PieceType.QUEEN));
                        validMoves.add(new ChessMove(startPosition,currPos,ChessPiece.PieceType.BISHOP));
                    }
                }
            }
            // You can move forward if there is no piece in front of you
            if(board.getPiece(new ChessPosition(row+1,col)) == null){
                ChessPosition currPos = new ChessPosition(row+1,col);
                if(row==7){
                    validMoves.add(new ChessMove(startPosition,currPos,ChessPiece.PieceType.ROOK));
                    validMoves.add(new ChessMove(startPosition,currPos,ChessPiece.PieceType.KNIGHT));
                    validMoves.add(new ChessMove(startPosition,currPos,ChessPiece.PieceType.QUEEN));
                    validMoves.add(new ChessMove(startPosition,currPos,ChessPiece.PieceType.BISHOP));
                }
                else{
                    validMoves.add(new ChessMove(startPosition,currPos,null));
                }
            }

            // you can also move two forward if you are on the starting row and there are no pieces in front of you
            if(row==2){
                // Make sure there isn't a piece one row in front blocking the piece
                if(board.getPiece(new ChessPosition(row+1,col)) == null){
                    if(board.getPiece(new ChessPosition(row+2,col)) == null){
                        ChessPosition currPos = new ChessPosition(row+2,col);
                        validMoves.add(new ChessMove(startPosition,currPos,null));
                    }
                }
            }
        }
        else{
            // Check if the pawn can go forward one, or capture a diagonal piece
            if(col+1 < 9){
                // diagonal right
                ChessPosition currPos = new ChessPosition(row-1,col+1);
                // Add the move if you can capture
                if(board.getPiece(currPos)!=null){
                    if(board.getPiece(currPos).getTeamColor() != myPiece.getTeamColor() && row-1 != 1){
                        validMoves.add(new ChessMove(startPosition,currPos,null));
                    }
                    else if(board.getPiece(currPos).getTeamColor() != myPiece.getTeamColor() && row-1 == 1){
                        validMoves.add(new ChessMove(startPosition,currPos,ChessPiece.PieceType.ROOK));
                        validMoves.add(new ChessMove(startPosition,currPos,ChessPiece.PieceType.KNIGHT));
                        validMoves.add(new ChessMove(startPosition,currPos,ChessPiece.PieceType.QUEEN));
                        validMoves.add(new ChessMove(startPosition,currPos,ChessPiece.PieceType.BISHOP));
                    }
                }
            }
            if(col-1 > 0){
                // diagonal left
                ChessPosition currPos = new ChessPosition(row-1,col-1);
                // Add the move if you can capture
                if(board.getPiece(currPos)!=null){
                    if(board.getPiece(currPos).getTeamColor() != myPiece.getTeamColor() && row-1 != 1){
                        validMoves.add(new ChessMove(startPosition,currPos,null));
                    }
                    else if(board.getPiece(currPos).getTeamColor() != myPiece.getTeamColor() && row-1 == 1){
                        validMoves.add(new ChessMove(startPosition,currPos,ChessPiece.PieceType.ROOK));
                        validMoves.add(new ChessMove(startPosition,currPos,ChessPiece.PieceType.KNIGHT));
                        validMoves.add(new ChessMove(startPosition,currPos,ChessPiece.PieceType.QUEEN));
                        validMoves.add(new ChessMove(startPosition,currPos,ChessPiece.PieceType.BISHOP));
                    }
                }
            }
            // You can move forward if there is no piece in front of you
            if(board.getPiece(new ChessPosition(row-1,col)) == null){
                ChessPosition currPos = new ChessPosition(row-1,col);
                if(row==2){
                    validMoves.add(new ChessMove(startPosition,currPos,ChessPiece.PieceType.ROOK));
                    validMoves.add(new ChessMove(startPosition,currPos,ChessPiece.PieceType.KNIGHT));
                    validMoves.add(new ChessMove(startPosition,currPos,ChessPiece.PieceType.QUEEN));
                    validMoves.add(new ChessMove(startPosition,currPos,ChessPiece.PieceType.BISHOP));
                }
                else{
                    validMoves.add(new ChessMove(startPosition,currPos,null));
                }
            }

            // you can also move two forward if you are on the starting row and there are no pieces in front of you
            if(row==7){
                // Make sure there isn't a piece one row in front blocking the piece
                if(board.getPiece(new ChessPosition(row-1,col)) == null){
                    if(board.getPiece(new ChessPosition(row-2,col)) == null){
                        ChessPosition currPos = new ChessPosition(row-2,col);
                        validMoves.add(new ChessMove(startPosition,currPos,null));
                    }
                }
            }
        }

        return validMoves;
    }
}