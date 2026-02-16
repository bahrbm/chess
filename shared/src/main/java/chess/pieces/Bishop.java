package chess.pieces;

import chess.*;

import java.util.Collection;
import java.util.LinkedList;

public class Bishop implements MoveCalc{
    private final ChessBoard board;
    private final ChessPosition startPosition;
    private final Collection<ChessMove> validMoves = new LinkedList<>();

    public Bishop( ChessBoard board, ChessPosition startPosition) {
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

        // Create lists to keep track of top and bottom positions for use when a piece blocks the path
        LinkedList<ChessMove> topMoves = new LinkedList<>();
        LinkedList<ChessMove> botMoves = new LinkedList<>();

        // Add booleans for when we look to the right side of the piece to not add any more pieces
        boolean botPieceFound = false;
        boolean topPieceFound = false;

        // Check all four diagonals
        for(int i = -7; i < 8; i++){

            // Skip the column that the piece is on
            if(i==0){
                topMoves.clear();
                botMoves.clear();
                continue;
            }
            // Only consider the columns that are on the board
            if(row + i < 1 || row + i > 8){
                continue;
            }

            // Get the top and bottom rows depending on which column you are in
            if(i < 0){
                ChessPosition topPos = new ChessPosition(row+i,col-i);
                ChessPosition botPos = new ChessPosition(row+i,col+i);

                // Check Bottom Piece
                if(col+i > 0){
                    if(board.getPiece(botPos) == null){
                        // If there is no piece, then add the move to the collection
                        ChessMove move = new ChessMove(startPosition,botPos,null);
                        validMoves.add(move);
                        botMoves.add(move);
                    }
                    else if(board.getPiece(botPos).getTeamColor() != myPiece.getTeamColor()){
                        // If there is an opposing piece, then we need to get rid of all previous botMoves
                        validMoves.removeAll(botMoves);
                        // Then we add the capture to the collection of valid moves
                        validMoves.add(new ChessMove(startPosition,botPos,null));
                        botMoves.add(new ChessMove(startPosition,botPos,null));
                    }
                    else{
                        // If there is a piece and it's our teams, then we just remove all previous botMoves
                        validMoves.removeAll(botMoves);
                    }
                }

                if(col-i < 9){
                    if(board.getPiece(topPos) == null){
                        // If there is no piece, then add the move to the collection
                        ChessMove move = new ChessMove(startPosition,topPos,null);
                        validMoves.add(move);
                        topMoves.add(move);
                    }
                    else if(board.getPiece(topPos).getTeamColor() != myPiece.getTeamColor()){
                        // If there is an opposing piece, then we need to get rid of all previous topMoves
                        validMoves.removeAll(topMoves);
                        // Then we add the capture to the collection of valid moves
                        validMoves.add(new ChessMove(startPosition,topPos,null));
                        topMoves.add(new ChessMove(startPosition,topPos,null));
                    }
                    else{
                        // If there is our team's piece, then we just remove all previous topMoves
                        validMoves.removeAll(topMoves);
                    }
                }
            }
            else{
                ChessPosition topPos = new ChessPosition(row+i,col+i);
                ChessPosition botPos = new ChessPosition(row+i,col-i);

                if(col-i > 0){
                    if(!topPieceFound){
                        if(board.getPiece(botPos) == null){
                            // If there is no piece, then add the move to the collection
                            ChessMove move = new ChessMove(startPosition,botPos,null);
                            validMoves.add(move);
                        }
                        else if(board.getPiece(botPos).getTeamColor() != myPiece.getTeamColor()){
                            // If there is an opposing piece then we can capture
                            validMoves.add(new ChessMove(startPosition,botPos,null));
                            topPieceFound = true;
                        }
                        else{
                            // If there is a piece and it's our teams, then just mark true
                            topPieceFound = true;
                        }
                    }
                }

                if(col+i < 9){
                    if(!botPieceFound){
                        if(board.getPiece(topPos) == null){
                            // If there is no piece, then add the move to the collection
                            ChessMove move = new ChessMove(startPosition,topPos,null);
                            validMoves.add(move);
                        }
                        else if(board.getPiece(topPos).getTeamColor() != myPiece.getTeamColor()){
                            // If there is an opposing piece then we capture
                            validMoves.add(new ChessMove(startPosition,topPos,null));
                            botPieceFound = true;
                        }
                        else{
                            // If we find our teams piece then set boolean to true to stop adding pieces
                            botPieceFound = true;
                        }
                    }
                }
            }
        }
        return validMoves;
    }
}
