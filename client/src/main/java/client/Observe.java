package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_BG_COLOR;
import static ui.EscapeSequences.SET_BG_COLOR_BLACK;
import static ui.EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
import static ui.EscapeSequences.SET_BG_COLOR_WHITE;
import static ui.EscapeSequences.SET_TEXT_BOLD;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLACK;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class Observe {
    private ChessBoard currBoard;

    public void setCurrGame(ChessGame currGame){
        this.currBoard = currGame.getBoard();
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
                   help - lists out all available commands
                """;
    }

    public String printGame(){
        for(int i = 9; i > -1; i--){

            if(i == 0 || i == 9){
                printWhiteBorder();
                continue;
            }

            for(int j = 0; j < 10; j++){

                // Keep for white
                ChessPosition currPos = new ChessPosition(i, j);

                if(j == 0 || j == 9){
                    System.out.print(SET_BG_COLOR_LIGHT_GREY);
                    System.out.printf(" %d ", i);
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

    private boolean isBlank(ChessPosition currPos){
        return currBoard.getPiece(currPos) == null;
    }

    private boolean isWhite(ChessPosition currPos){
        return currBoard.getPiece(currPos).getTeamColor() == ChessGame.TeamColor.WHITE;
    }

}