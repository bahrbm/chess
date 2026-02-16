package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable{

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
     * Removes a chess piece from the chess board
     *
     * @param position where to remove the piece from
     */
    public void removePiece(ChessPosition position){
        squares[position.getRow()-1][position.getColumn()-1] = null;
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
        ChessPiece blackPawn = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.PAWN);
        ChessPiece whitePawn = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.PAWN);

        // Create Kings
        ChessPiece blackKing = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.KING);
        ChessPiece whiteKing = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.KING);

        // Create Queens
        ChessPiece blackQueen = new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.QUEEN);
        ChessPiece whiteQueen = new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.QUEEN);

        // Create Bishops
        ChessPiece blackBishop = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        ChessPiece whiteBishop = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);

        // Create Knights
        ChessPiece blackKnight = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        ChessPiece whiteKnight = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);

        // Create Rooks
        ChessPiece blackRook = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        ChessPiece whiteRook = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);

        // Place Pieces by moving from left to right on the chess board.
        for(int i =1; i < 9; i++){

            // Get current position
            ChessPosition whiteFrontPosition = new ChessPosition(2,i);
            ChessPosition whiteBackPosition  = new ChessPosition(1,i);
            ChessPosition blackFrontPosition = new ChessPosition(7,i);
            ChessPosition blackBackPosition  = new ChessPosition(8,i);

            // Add Pawns to the front
            addPiece(whiteFrontPosition,whitePawn);
            addPiece(blackFrontPosition, blackPawn);

            // Logic to add the correct pieces to the back
            if(i==1 || i==8) {
                // Add Rooks
                addPiece(whiteBackPosition, whiteRook);
                addPiece(blackBackPosition, blackRook);
            }
            else if(i==2 || i==7){
                // Add Knights
                addPiece(whiteBackPosition, whiteKnight);
                addPiece(blackBackPosition, blackKnight);
            }
            else if (i==3 || i==6){
                // Add Bishops
                addPiece(whiteBackPosition, whiteBishop);
                addPiece(blackBackPosition, blackBishop);
            }
            else if (i==4){
                // Add Queens
                addPiece(whiteBackPosition, whiteQueen);
                addPiece(blackBackPosition, blackQueen);
            }
            else{
                // Add Kings
                addPiece(whiteBackPosition, whiteKing);
                addPiece(blackBackPosition, blackKing);
            }
        }
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                squares[7][0] + "|" + squares[7][1] + "|" + squares[7][2] + "|" + squares[7][3] + "|" + squares[7][4] + "|" + squares[7][5] + "|" + squares[7][6] + "|" + squares[7][7] + "\n" +
                squares[6][0] + "|" + squares[6][1] + "|" + squares[6][2] + "|" + squares[6][3] + "|" + squares[6][4] + "|" + squares[6][5] + "|" + squares[6][6] + "|" + squares[6][7] + "\n" +
                squares[5][0] + "|" + squares[5][1] + "|" + squares[5][2] + "|" + squares[5][3] + "|" + squares[5][4] + "|" + squares[5][5] + "|" + squares[5][6] + "|" + squares[5][7] + "\n" +
                squares[4][0] + "|" + squares[4][1] + "|" + squares[4][2] + "|" + squares[4][3] + "|" + squares[4][4] + "|" + squares[4][5] + "|" + squares[4][6] + "|" + squares[4][7] + "\n" +
                squares[3][0] + "|" + squares[3][1] + "|" + squares[3][2] + "|" + squares[3][3] + "|" + squares[3][4] + "|" + squares[3][5] + "|" + squares[3][6] + "|" + squares[3][7] + "\n" +
                squares[2][0] + "|" + squares[2][1] + "|" + squares[2][2] + "|" + squares[2][3] + "|" + squares[2][4] + "|" + squares[2][5] + "|" + squares[2][6] + "|" + squares[2][7] + "\n" +
                squares[1][0] + "|" + squares[1][1] + "|" + squares[1][2] + "|" + squares[1][3] + "|" + squares[1][4] + "|" + squares[1][5] + "|" + squares[1][6] + "|" + squares[1][7] + "\n" +
                squares[0][0] + "|" + squares[0][1] + "|" + squares[0][2] + "|" + squares[0][3] + "|" + squares[0][4] + "|" + squares[0][5] + "|" + squares[0][6] + "|" + squares[0][7] + "\n" +
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

    @Override
    public ChessBoard clone() {
        try {
            ChessBoard clone = (ChessBoard) super.clone();

            ChessPiece [][] clonedBoard = new ChessPiece[8][8];

            // Loop through all the pieces and add them to the clone board;
            for(int i = 1; i < 9; i++){
                for(int j = 1; j < 9; j++){

                    // Account for null
                    if(this.squares[i-1][j-1] == null){
                        continue;
                    }

                    clonedBoard[i-1][j-1] = this.squares[i-1][j-1].clone();
                }
            }

            clone.squares = clonedBoard;

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
