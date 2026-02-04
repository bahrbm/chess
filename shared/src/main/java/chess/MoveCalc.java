package chess;

import java.util.Collection;

public abstract interface MoveCalc {


    public Collection<ChessMove> findMoves();
}
