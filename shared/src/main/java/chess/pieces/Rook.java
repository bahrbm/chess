package chess.pieces;

import chess.*;
import chess.MoveCalc;

import java.util.Collection;
import java.util.LinkedList;

public class Rook implements MoveCalc {
    private final ChessBoard board;
    private final ChessPosition startPosition;
    private final Collection<ChessMove> validMoves = new LinkedList<>();
    private final ChessGame.TeamColor myTeam;

    public Rook(ChessBoard board, ChessPosition startPosition){
        this.board = board;
        this.startPosition = startPosition;
        this.myTeam = board.getPiece(startPosition).getTeamColor();
    }


    @Override
    public Collection<ChessMove> findMoves() {

        // Get the current piece so we can get the color, and get the adjusted row and column for indexing
        int row = startPosition.getRow();
        int col = startPosition.getColumn();

        // Check each direction and add moves as needed
        checkRight(row,col);
        checkLeft(row,col);
        checkAbove(row,col);
        checkBelow(row,col);
        return validMoves;
    }

    void checkRight(int row, int col){

        int currCol = col + 1;
        // increment column until we find a piece
        while(currCol < 9){

            ChessPosition currPos = new ChessPosition(row,currCol);

            //Check if the space is empty
            if(board.getPiece(currPos)==null){
                validMoves.add(new ChessMove(startPosition,currPos,null));
            }
            // If there is a piece there but it is the other team's then capture
            else if(board.getPiece(currPos).getTeamColor()!=myTeam){
                validMoves.add(new ChessMove(startPosition,currPos,null));
                return;
            }
            else{
                return;
            }

            currCol += 1;
        }
    }

    void checkLeft(int row, int col){
        int currCol = col - 1;
        // increment column until we find a piece
        while(currCol > 0){

            ChessPosition currPos = new ChessPosition(row,currCol);

            //Check if the space is empty
            if(board.getPiece(currPos)==null){
                validMoves.add(new ChessMove(startPosition,currPos,null));
            }
            // If there is a piece there but it is the other team's then capture
            else if(board.getPiece(currPos).getTeamColor()!=myTeam){
                validMoves.add(new ChessMove(startPosition,currPos,null));
                return;
            }
            else{
                return;
            }

            currCol -= 1;
        }
    }

    void checkAbove(int row, int col){
        int currRow = row + 1;
        // increment column until we find a piece
        while(currRow < 9){

            ChessPosition currPos = new ChessPosition(currRow,col);

            //Check if the space is empty
            if(board.getPiece(currPos)==null){
                validMoves.add(new ChessMove(startPosition,currPos,null));
            }
            // If there is a piece there but it is the other team's then capture
            else if(board.getPiece(currPos).getTeamColor()!=myTeam){
                validMoves.add(new ChessMove(startPosition,currPos,null));
                return;
            }
            else{
                return;
            }

            currRow += 1;
        }
    }

    void checkBelow(int row, int col){
        int currRow = row - 1;
        // increment column until we find a piece
        while(currRow > 0){

            ChessPosition currPos = new ChessPosition(currRow,col);

            //Check if the space is empty
            if(board.getPiece(currPos)==null){
                validMoves.add(new ChessMove(startPosition,currPos,null));
            }
            // If there is a piece there but it is the other team's then capture
            else if(board.getPiece(currPos).getTeamColor()!=myTeam){
                validMoves.add(new ChessMove(startPosition,currPos,null));
                return;
            }
            else{
                return;
            }

            currRow -= 1;
        }
    }
}
