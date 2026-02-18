package chess.pieces;

import chess.*;

import java.util.Collection;
import java.util.LinkedList;

public class Bishop implements MoveCalc{
    private final ChessBoard board;
    private final ChessPosition startPosition;
    private final Collection<ChessMove> validMoves = new LinkedList<>();
    private final ChessGame.TeamColor myTeam;

    public Bishop( ChessBoard board, ChessPosition startPosition) {
        this.startPosition = startPosition;
        this.board = board;
        this.myTeam = board.getPiece(startPosition).getTeamColor();
    }

    @Override
    public Collection<ChessMove> findMoves() {

        // Get the adjusted row and column for indexing
        int row = startPosition.getRow();
        int col = startPosition.getColumn();

        topLeft(row,col);
        topRight(row,col);
        botLeft(row,col);
        botRight(row,col);

        return validMoves;
    }

    void topLeft(int row, int col){
        row += 1;
        col -= 1;
        while(row < 9 && col > 0){

            ChessPosition currPos = new ChessPosition(row,col);

            if(board.getPiece(currPos)==null){
                validMoves.add(new ChessMove(startPosition,currPos,null));
            }
            else if(board.getPiece(currPos).getTeamColor() != myTeam){
                validMoves.add(new ChessMove(startPosition,currPos,null));
                return;
            }
            else{
                return;
            }

            row += 1;
            col -= 1;
        }
    }

    void topRight(int row, int col){
        row += 1;
        col += 1;
        while(row < 9 && col < 9){

            ChessPosition currPos = new ChessPosition(row,col);

            if(board.getPiece(currPos)==null){
                validMoves.add(new ChessMove(startPosition,currPos,null));
            }
            else if(board.getPiece(currPos).getTeamColor() != myTeam){
                validMoves.add(new ChessMove(startPosition,currPos,null));
                return;
            }
            else{
                return;
            }

            row += 1;
            col += 1;
        }
    }

    void botLeft(int row, int col){
        row -= 1;
        col -= 1;
        while(row > 0 && col > 0){

            ChessPosition currPos = new ChessPosition(row,col);

            if(board.getPiece(currPos)==null){
                validMoves.add(new ChessMove(startPosition,currPos,null));
            }
            else if(board.getPiece(currPos).getTeamColor() != myTeam){
                validMoves.add(new ChessMove(startPosition,currPos,null));
                return;
            }
            else{
                return;
            }

            row -= 1;
            col -= 1;
        }
    }

    void botRight(int row, int col){
        row -= 1;
        col += 1;

        while(row > 0 && col < 9){

            ChessPosition currPos = new ChessPosition(row,col);

            if(board.getPiece(currPos)==null){
                validMoves.add(new ChessMove(startPosition,currPos,null));
            }
            else if(board.getPiece(currPos).getTeamColor() != myTeam){
                validMoves.add(new ChessMove(startPosition,currPos,null));
                return;
            }
            else{
                return;
            }

            row -= 1;
            col += 1;
        }
    }

}
