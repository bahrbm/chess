package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {

        // Create all the pieces that need to be placed

        // Create Pawns
        ChessPiece b_pawn = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.PAWN);
        ChessPiece w_pawn = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.PAWN);

        // Create Kings
        ChessPiece b_king = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.KING);
        ChessPiece w_king = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.KING);

        // Create Queens
        ChessPiece b_queen = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.QUEEN);
        ChessPiece w_queen = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.QUEEN);

        // Create Bishops
        ChessPiece b_bishop = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        ChessPiece w_bishop = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);

        // Create Knights
        ChessPiece b_knight = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        ChessPiece w_knight = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);

        // Create Rooks
        ChessPiece b_rook = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        ChessPiece w_rook = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);

        // Place Pieces by moving from left to right on the chess board.
        for(int i =1; i < 9; i++){

            // Get current position
            ChessPosition white_front_position = new ChessPosition(2,i);
            ChessPosition white_back_position  = new ChessPosition(1,i);
            ChessPosition black_front_position = new ChessPosition(7,i);
            ChessPosition black_back_position  = new ChessPosition(8,i);

            // Add Pawns to the front
            addPiece(white_front_position,w_pawn);
            addPiece(black_front_position,b_pawn);

            // Logic to add the correct pieces to the back
            if(i==1 || i==8) {
                // Add Rooks
                addPiece(white_back_position, w_rook);
                addPiece(black_back_position, b_rook);
            }
            else if(i==2 || i==7){
                // Add Knights
                addPiece(white_back_position, w_knight);
                addPiece(black_back_position, b_knight);
            }
            else if (i==3 || i==6){
                // Add Bishops
                addPiece(white_back_position, w_bishop);
                addPiece(black_back_position, b_bishop);
            }
            else if (i==4){
                // Add Queens
                addPiece(white_back_position, w_queen);
                addPiece(black_back_position, b_queen);
            }
            else{
                // Add Kings
                addPiece(white_back_position, w_king);
                addPiece(black_back_position, b_king);
            }
        }
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "squares=" + Arrays.toString(squares) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || (this.getClass() != o.getClass())) {
            return false;
        }

        if (o == this){
            return true;
        }

        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }
}
