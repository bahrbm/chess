package chess;

import java.util.Collection;
import java.util.List;

public abstract interface MoveCalc {


    public Collection<ChessMove> findMoves();
}
