package server;

import chess.*;
import dataaccess.*;
import service.*;

public class ServerMain {
    static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        try {
            var port = 8080;
            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }

            UserDAO userDAO = new SQLUserDAO();
            GameDAO gameDAO = new MemoryGameDAO();
            AuthDAO authDAO = new SQLAuthDAO();

            if (args.length >= 2 && args[1].equals("sql")) {
                userDAO = new SQLUserDAO();
                gameDAO = new SQLGameDAO();
                authDAO = new SQLAuthDAO();
            }

            var userService  = new UserService(userDAO, authDAO);
            var gameService  = new GameService(gameDAO);
            var clearService = new ClearService(userDAO, authDAO, gameDAO);
            var server = new Server(userService, gameService, clearService).run(port);
            System.out.printf("Server started on port %d with %s, %s, %s%n", port, userDAO.getClass(), gameDAO.getClass(), authDAO.getClass());
            return;
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
        System.out.println("""
                Chess:
                java ServerMain <port> [sql]
                """);
    }
}
