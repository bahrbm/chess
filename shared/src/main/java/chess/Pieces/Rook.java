package chess.Pieces;

import chess.*;
import chess.MoveCalc;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Rook implements MoveCalc {
    private final ChessBoard board;
    private final ChessPosition startPosition;
    private final Collection<ChessMove> validMoves = new LinkedList<>();

    public Rook(ChessBoard board, ChessPosition startPosition){
        this.board = board;
        this.startPosition = startPosition;
    }


    @Override
    public Collection<ChessMove> findMoves() {

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
        return validMoves;
    }
}
