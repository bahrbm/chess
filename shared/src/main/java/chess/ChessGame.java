package chess;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame implements Cloneable{

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

    @Override
    public ChessGame clone() {
        try {
            // Make a shallow copy
            ChessGame clone = (ChessGame) super.clone();

            // Need to make a deep copy of the board as it is mutable
            ChessBoard clonedBoard = getBoard().clone();
            clone.setBoard(clonedBoard);
            return clone;

        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
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

        // Get all the possible moves that the piece can make
        Collection<ChessMove> allMoves = currPiece.pieceMoves(board,startPosition);
        Collection<ChessMove> validMoves = new LinkedList<>();

        // Now we need to loop through all the moves and get rid of any that put our king in check
        for(ChessMove move : allMoves){
            boolean check = checkMove(move);
            // Add the move to the list of valid moves if my King is not in check by the end of the turn
            if(!check){
                validMoves.add(move);
            }
        }

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        // Get all the needed information about the move
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece.PieceType promotionPieceType = move.getPromotionPiece();
        ChessPiece currPiece = board.getPiece(startPosition);

        // Initial Error Handling
        if(board.getPiece(startPosition) == null){
            // Check if the player is trying to move a piece
            throw new InvalidMoveException();
        }

        ChessGame.TeamColor currTeam = currPiece.getTeamColor();

        if(currTeam != currTurn){
            // Check if the player is attempting to move out of turn
            throw new InvalidMoveException();
        }

        // Get a Collection of all valid moves that the piece can make
        Collection<ChessMove> moves = validMoves(startPosition);

        // If the current move that the user is trying to make isn't a move that piece can make, throw an error
        if(!moves.contains(move)){
            throw new InvalidMoveException();
        }

        // Check if the move involves promoting a pawn
        if(move.getPromotionPiece()!=null){
            ChessPiece promotionPiece = new ChessPiece(currTeam,promotionPieceType);

            // Move the current Piece to the end position
            board.addPiece(endPosition,promotionPiece);
        }
        else{
            // Move the current Piece to the end position
            board.addPiece(endPosition,currPiece);
        }

        // At the end of the move we need to remove the current piece from the starting position and update whose turn it is
        board.removePiece(startPosition);
        updateTurn();

    }

    boolean checkMove(ChessMove move){

        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece myPiece = board.getPiece(startPosition);
        TeamColor currTeam = board.getPiece(startPosition).getTeamColor();

        // Create a clone of the board to revert back to
        ChessBoard tempBoard = board.clone();

        // Make the Move
        board.addPiece(endPosition, myPiece);
        board.removePiece(startPosition);

        // System.out.println(board);
        // See if making this move puts my King in Check
        boolean check = isInCheck(currTeam);

        // Revert the board to its previous state
        board = tempBoard;

        // Return check
        return check;
    }

    void updateTurn(){
        if(currTurn == TeamColor.WHITE){
            currTurn = TeamColor.BLACK;
        }
        else{
            currTurn = TeamColor.WHITE;
        }
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

        // First we need to see if the current team is in check
        if(!isInCheck(teamColor)){
            return false;
        }

        // Now we need to loop through all possible moves that the team can make to see if it can get the king out of check
        for(int i = 1; i < 9; i++){
            for(int j = 1; j < 9; j++){

                ChessPosition currPos = new ChessPosition(i,j);

                // Skip if there isn't a piece in the current position
                if(board.getPiece(currPos)==null){
                    continue;
                }

                // Skip if the piece is on the other team
                if(board.getPiece(currPos).getTeamColor()!=teamColor){
                    continue;
                }

                // Get the moves that the piece can make
                Collection<ChessMove> potentialMoves = validMoves(currPos);

                // If there is a move, return false
                if(!potentialMoves.isEmpty()){
                    return false;
                }
            }
        }

        // if you go through all the pieces and there aren't any moves available, return true
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // First we need to see if the current team is in check
        if(isInCheck(teamColor)){
            return false;
        }

        // Now we need to loop through all possible moves that the team can make to see if there is an available move
        for(int i = 1; i < 9; i++){
            for(int j = 1; j < 9; j++){

                ChessPosition currPos = new ChessPosition(i,j);

                // Skip if there isn't a piece in the current position
                if(board.getPiece(currPos)==null){
                    continue;
                }

                // Skip if the piece is on the other team
                if(board.getPiece(currPos).getTeamColor()!=teamColor){
                    continue;
                }

                // Get the moves that the piece can make
                Collection<ChessMove> potentialMoves = validMoves(currPos);

                // If there is a move, return false
                if(!potentialMoves.isEmpty()){
                    return false;
                }
            }
        }

        // if you go through all the pieces and there aren't any moves available, return true
        return true;

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
