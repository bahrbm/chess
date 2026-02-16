package chess;

import chess.pieces.*;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece implements Cloneable{

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public ChessPiece clone() {
        try {
            // We just need a shallow copy as enums are immutable
            return (ChessPiece) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (type == PieceType.KING) {
            MoveCalc moves = new King(board, myPosition);
            return moves.findMoves();
        } else if (type == PieceType.BISHOP) {
            MoveCalc moves = new Bishop(board, myPosition);
            return moves.findMoves();
        } else if (type == PieceType.ROOK) {
            MoveCalc moves = new Rook(board, myPosition);
            return moves.findMoves();
        } else if (type == PieceType.KNIGHT) {
            MoveCalc moves = new Knight(board, myPosition);
            return moves.findMoves();
        } else if (type == PieceType.PAWN) {
            MoveCalc moves = new Pawn(board, myPosition);
            return moves.findMoves();
        } else {
            MoveCalc moves = new Queen(board, myPosition);
            return moves.findMoves();
        }
    }

    @Override
    public String toString() {
        if(pieceColor == ChessGame.TeamColor.WHITE){
            if(type == PieceType.KING){
                return "K";
            }
            else if(type == PieceType.QUEEN){
                return "Q";
            }
            else if(type == PieceType.ROOK){
                return "R";
            }
            else if(type == PieceType.KNIGHT){
                return "N";
            }
            else if(type == PieceType.BISHOP){
                return "B";
            }
            else if(type == PieceType.PAWN){
                return "P";
            }
        }
        else{
            if(type == PieceType.KING){
                return "k";
            }
            else if(type == PieceType.QUEEN){
                return "q";
            }
            else if(type == PieceType.ROOK){
                return "r";
            }
            else if(type == PieceType.KNIGHT){
                return "n";
            }
            else if(type == PieceType.BISHOP){
                return "b";
            }
            else if(type == PieceType.PAWN){
                return "p";
            }
        }
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return (pieceColor == that.pieceColor && type == that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
