package client;

import chess.*;
import exception.DataAccessException;
import request.MakeMoveRequest;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl {
    private final ChessGame currGame;
    private ChessGame.TeamColor team;
    private final ChessBoard currBoard;
    private final ServerFacade server;
    private int gameID;

    public Repl(ChessGame currGame, ServerFacade server, int gameID){
        this.currGame  = currGame;
        this.server    = server;
        this.currBoard = currGame.getBoard();
        this.gameID    = gameID;
    }

    public void setTeam(ChessGame.TeamColor team){
        this.team = team;
    }

    public void run(){

        System.out.print(SET_TEXT_COLOR_GREEN);
        System.out.println("♕ Welcome to the game. Enter 'help' for a list of commands. ♕");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        printGame();

        while(!result.equals("leave")){

            System.out.print(SET_TEXT_COLOR_WHITE + " >>> ");
            String line = scanner.nextLine();

            try{
                result = eval(line);
                if(!Objects.equals(result, "leave")){
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                }
            } catch(Throwable e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }


    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "redraw" -> printGame();
                case "move" -> makeMove(params);
                case "leave" -> "leave";
                default -> help();
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        return SET_TEXT_COLOR_BLUE + """
                   redraw - redraw the current game
                   leave - leave game (allows someone else to take your place)
                   move <ROW> <COL> <ROW> <COL> - make a move
                   resign - surrender and end the game
                   highlight <ROW> <COL> - highlight all available moves for the current piece
                   help - lists out all available commands
                """;
    }

    public String makeMove(String... params) throws InvalidMoveException, DataAccessException {

        int startRow = Integer.parseInt(params[0]);
        int startCol = Integer.parseInt(params[1]);
        int endRow   = Integer.parseInt(params[2]);
        int endCol   = Integer.parseInt(params[3]);

        ChessMove requestedMove = new ChessMove(new ChessPosition(startRow, startCol),
                                                new ChessPosition(endRow, endCol),
                                   null);

        currGame.makeMove(requestedMove);

        server.makeMove(new MakeMoveRequest(gameID, requestedMove));

        return "";
    }

    public String printGame(){
        for(int i = 9; i > -1; i--){

            if(i == 0 || i == 9){
                if(team == ChessGame.TeamColor.WHITE){
                    printWhiteBorder();
                }
                else{
                    printBlackBorder();
                }
                continue;
            }

            for(int j = 0; j < 10; j++){

                // Keep for white
                ChessPosition currPos = new ChessPosition(i, j);

                if(team == ChessGame.TeamColor.BLACK){
                    currPos.setPosition(9 - i, 9 - j);
                }

                if(j == 0 || j == 9){
                    System.out.print(SET_BG_COLOR_LIGHT_GREY);
                    if(team == ChessGame.TeamColor.WHITE){
                        System.out.printf(" %d ", i);
                    }
                    else{
                        System.out.printf(" %d ", 9 - i);
                    }

                }
                else{
                    if((i + j) % 2 == 0){
                        System.out.print(SET_BG_COLOR_BLACK);
                    }
                    else{
                        System.out.print(SET_BG_COLOR_WHITE);
                    }

                    if(isBlank(currPos)){
                        System.out.print("   ");
                    }
                    else if(isWhite(currPos)){
                        System.out.print(SET_TEXT_COLOR_RED + SET_TEXT_BOLD);
                        System.out.print(" " + currBoard.getPiece(currPos).toString() + " ");
                        System.out.print(SET_TEXT_COLOR_BLACK);
                    }
                    else{
                        System.out.print(SET_TEXT_COLOR_BLUE + SET_TEXT_BOLD);
                        System.out.print(" " + currBoard.getPiece(currPos).toString() + " ");
                        System.out.print(SET_TEXT_COLOR_BLACK);
                    }
                }

                if(j == 9){
                    System.out.println(RESET_BG_COLOR);
                }
            }
        }

        return "";
    }

    public void printWhiteBorder(){
        System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);
        System.out.print("    a  b  c  d  e  f  g  h    " + RESET_BG_COLOR + "\n");
    }

    public void printBlackBorder(){
        System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);
        System.out.print("    h  g  f  e  d  c  b  a    " + RESET_BG_COLOR + "\n");
    }

    private boolean isBlank(ChessPosition currPos){
        return currBoard.getPiece(currPos) == null;
    }

    private boolean isWhite(ChessPosition currPos){
        return currBoard.getPiece(currPos).getTeamColor() == ChessGame.TeamColor.WHITE;
    }
}
