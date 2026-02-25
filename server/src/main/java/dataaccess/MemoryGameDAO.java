package dataaccess;

import chess.ChessGame;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{

    private final HashMap<String, ChessGame> games = new HashMap<>();

    @Override
    public void clearGameData() {
        games.clear();
    }
}
