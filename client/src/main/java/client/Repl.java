package client;

import chess.*;
import exception.DataAccessException;
import exception.ResponseException;

import java.util.Arrays;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl {
    private ChessGame currGame;
    private ChessGame.TeamColor team;
    private ChessBoard currBoard;

    public void setCurrGame(ChessGame currGame){
        this.currGame = currGame;
        this.currBoard = currGame.getBoard();
    }

    public void setTeam(ChessGame.TeamColor team){
        this.team = team;
    }

    public void run(){

        System.out.print(SET_TEXT_COLOR_GREEN);
        System.out.println("♕ Welcome to the game. Enter 'quit' to leave. ♕");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("quit")){
            printGame();
            System.out.print(SET_TEXT_COLOR_WHITE + " >>> ");
            String line = scanner.nextLine();

            try{
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
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
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        return SET_TEXT_COLOR_BLUE + """
                   redraw - redraw the current game
                   leave - leave game and allow someone else to take your place
                   move <ROW> <COL> <ROW> <COL> - make a move
                   resign - surrender and end the game
                   highlight <ROW> <COL> - highlight all available moves for the current piece
                   help - lists out all available commands
                   quit - exit program
                """;
    }

    public void printGame(){
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
