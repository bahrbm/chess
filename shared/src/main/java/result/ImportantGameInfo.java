package result;

import chess.ChessGame;

public record ImportantGameInfo(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame currGame) {
}
