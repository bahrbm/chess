package chess.Pieces;

import chess.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
        findDiagonalMoves();
        findRookMoves();
        return validMoves;
    }

    public void findDiagonalMoves(){
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
    }

    public void findRookMoves(){
        // Get the current piece so we can get the color, and get the adjusted row and column for indexing
        ChessPiece myPiece = board.getPiece(startPosition);
        int row = startPosition.getRow();
        int col = startPosition.getColumn();

        LinkedList<ChessMove> botMoves = new LinkedList<>();
        LinkedList<ChessMove> leftMoves = new LinkedList<>();
        boolean topPieceFound = false;
        boolean rightPieceFound = false;

        // Go column by column
        for(int i = 1; i < 9; i++){

            // if you are on the same column as the rook, go from bottom to top
            if(i == col){
                for(int j = 1; j < 9; j++){
                    ChessPosition currPos = new ChessPosition(j,i);

                    // Skip the rook
                    if(j == row){
                        continue;
                    }

                    //Check below the rook
                    if(j < row){

                        //If there isn't a piece add it to valid moves and botMoves
                        if(board.getPiece(currPos)==null){
                            ChessMove move = new ChessMove(startPosition, currPos, null);
                            validMoves.add(move);
                            botMoves.add(move);
                        }
                        // If there is a piece and it's the other team, capture and delete all moves below
                        else if(board.getPiece(currPos).getTeamColor() != myPiece.getTeamColor()){
                            ChessMove move = new ChessMove(startPosition, currPos, null);
                            validMoves.add(move);
                            botMoves.add(move);
                        }
                        // If there is a piece and it's our teams, delete all moves below
                        else{
                            validMoves.removeAll(botMoves);
                        }
                    }
                    // Check above the rook
                    else{
                        if(!topPieceFound){
                            //If there isn't a piece add it to valid moves and botMoves
                            if(board.getPiece(currPos)==null){
                                ChessMove move = new ChessMove(startPosition, currPos, null);
                                validMoves.add(move);
                            }
                            // If there is a piece and it's the other team, capture and delete all moves above
                            else if(board.getPiece(currPos).getTeamColor() != myPiece.getTeamColor()){
                                ChessMove move = new ChessMove(startPosition, currPos, null);
                                validMoves.add(move);
                                topPieceFound = true;
                            }
                            // If there is a piece and it's our teams, delete all moves above
                            else{
                                topPieceFound = true;
                            }
                        }
                    }
                }
            }
            // Looking to the left of the rook
            else if(i < col){
                // Same row as the rook, but different column
                ChessPosition currPos = new ChessPosition(row,i);
                //If there isn't a piece add it to valid moves and botMoves
                if(board.getPiece(currPos)==null){
                    ChessMove move = new ChessMove(startPosition, currPos, null);
                    validMoves.add(move);
                    leftMoves.add(move);
                }
                // If there is a piece and it's the other team, capture and delete all moves above
                else if(board.getPiece(currPos).getTeamColor() != myPiece.getTeamColor()){
                    ChessMove move = new ChessMove(startPosition, currPos, null);
                    validMoves.add(move);
                    leftMoves.add(move);
                }
                // If there is a piece and it's our teams, delete all moves above
                else{
                    validMoves.removeAll(leftMoves);
                }
            }
            // Check to the right of the rook
            else{
                ChessPosition currPos = new ChessPosition(row,i);
                if(!rightPieceFound){
                    //If there isn't a piece add it to valid moves and botMoves
                    if(board.getPiece(currPos)==null){
                        ChessMove move = new ChessMove(startPosition, currPos, null);
                        validMoves.add(move);
                    }
                    // If there is a piece and it's the other team, capture and delete all moves above
                    else if(board.getPiece(currPos).getTeamColor() != myPiece.getTeamColor()){
                        ChessMove move = new ChessMove(startPosition, currPos, null);
                        validMoves.add(move);
                        rightPieceFound = true;
                    }
                    // If there is a piece and it's our teams, delete all moves above
                    else{
                        rightPieceFound = true;
                    }
                }
            }
        }
    }
}