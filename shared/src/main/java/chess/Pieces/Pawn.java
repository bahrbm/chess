//package chess.Pieces;
//
//import chess.*;
//import java.util.Collection;
//import java.util.List;
//
//public class Pawn implements MoveCalc {
//
//    private final ChessPosition startPosition;
//    private final ChessBoard board;
//
//    public Pawn( ChessBoard board, ChessPosition startPosition) {
//        this.startPosition = startPosition;
//        this.board = board;
//    }
//
//    public Collection<ChessMove> findMoves(){
//
//        // Get the chess piece so that we can determine the moves depending on what color the piece is
//        ChessPiece myPiece = board.getPiece(startPosition);
//
//        // Check to see if the pawn can capture a piece that is on it's diagonal
//        boolean capture = canCapture(myPiece);
//
////        if((myPiece.getTeamColor() == ChessGame.TeamColor.WHITE) && startPosition.getRow() == 2){
////            if(this.startPostion.getColumn() != 1){
////                return
////            }
////            return whiteStartMoves();
////        }
//
//        return List.of();
//    }
//
//    public Collection<ChessMove> whitStartMoves(){
//        return List.of();
//    }
//
//    public boolean canCapture(ChessPiece myPiece){
//        boolean leftEdge;
//        boolean rightEdge;
//
//        // get the position of the left diagonal and right diagonal so we can check if there is a piece there
//        if(myPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
//            ChessPosition left = new ChessPosition(startPosition.getRow() + 1, startPosition.getColumn() - 1);
//            ChessPosition right = new ChessPosition(startPosition.getRow() + 1, startPosition.getColumn() + 1);
//        }
//        else{
//            ChessPosition left = new ChessPosition(startPosition.getRow() - 1, startPosition.getColumn() - 1);
//            ChessPosition right = new ChessPosition(startPosition.getRow() - 1, startPosition.getColumn() + 1);
//        }
//
//        // Check if the piece is at the leftmost edge
//        if(startPosition.getColumn() == 1){
//            leftEdge = true;
//            rightEdge = false;
//        }
//        // Check if the piece is at the rightmost edge
//        else if(startPosition.getColumn() == 8){
//            leftEdge = false;
//            rightEdge = true;
//        }
//        else{
//            leftEdge = false;
//            rightEdge = false;
//        }
//
//        // Check captures for white piece
//        if(leftEdge){
//            if(board.getPiece(right) == null){
//                return false;
//            }
//            else{
//                return true;
//            }
//        }
//        else if(rightEdge){
//            if(board.getPiece(left) == null){
//                return false;
//            }
//            else{
//                return true;
//            }
//        }
//        else if(board.getPiece(left) == null && board.getPiece(right) == null){
//            return false;
//        }
//        else{
//            return true;
//        }
//    }
//}
