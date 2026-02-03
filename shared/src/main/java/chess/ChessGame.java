package chess;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessGame.TeamColor currTurn;
    private ChessBoard board = new ChessBoard();

    public ChessGame() {
        currTurn = TeamColor.WHITE;
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {

        // First check if there is a piece at startPosition
        if(board.getPiece(startPosition)==null){
            return null;
        }

        ChessPiece currPiece = board.getPiece(startPosition);

        // Next we need to see if the team is in check or not
        if(isInCheck(currPiece.getTeamColor())){
            // TODO implement way to get rid of moves if the team is in check
            return List.of();
        }
        else{
            return currPiece.pieceMoves(board,startPosition);
        }

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        // Loop through the board to check the pieces at each position
        for(int i = 1; i < 9; i++){
            for(int j = 1; j < 9; j++){

                ChessPosition currPos = new ChessPosition(i,j);

                // Check if there is a piece in the current position we are considering or if the piece is on my team
                if(board.getPiece(currPos)==null || board.getPiece(currPos).getTeamColor() == teamColor){
                    continue;
                }

                ChessPiece currPiece = board.getPiece(currPos);

                Collection<ChessMove> currMoves;
                currMoves = currPiece.pieceMoves(board,currPos);

                // Go through each move and check if my king is in the end position of any of the possible moves
                for(ChessMove move : currMoves){

                    // First we need to check if there is a piece in the end position
                    if(board.getPiece(move.getEndPosition())==null){
                        continue;
                    }

                    // If there is a piece in the end position, check if it is a king
                    if(board.getPiece(move.getEndPosition()).getPieceType() == ChessPiece.PieceType.KING){
                        return true;
                    }
                }
            }
        }

        // If you didn't find a piece that puts this team in check, return false
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return currTurn == chessGame.currTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currTurn, board);
    }
}
