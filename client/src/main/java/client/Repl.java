package client;

import chess.ChessGame;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl {
    private ChessGame currGame;
    private ChessGame.TeamColor team;

    public void setCurrGame(ChessGame currGame){
        this.currGame = currGame;
    }

    public void setTeam(ChessGame.TeamColor team){
        this.team = team;
    }

    public void run(){

        Scanner scanner = new Scanner(System.in);
        String line = "";
        while(!line.equals("quit")){
            System.out.println("♕ Welcome to the game. Enter 'quit' to leave. ♕");
            if(team == ChessGame.TeamColor.WHITE){
                printWhiteGame();
            }
            else{
                printBlackGame();
            }
            System.out.print(SET_TEXT_COLOR_WHITE + " >>> ");
            line = scanner.nextLine();
        }
    }

    public void printWhiteGame(){
        for(int i = 0; i < 10; i++){

            if(i == 0 || i == 9){
                printWhiteBorder();
                continue;
            }

            for(int j = 0; j < 10; j++){

                if(j == 0 || j == 9){
                    System.out.print(SET_BG_COLOR_LIGHT_GREY);
                    System.out.printf(" %d ",9-i);
                }
                else{
                    System.out.print("   ");
                }

                if(j == 9){
                    System.out.println(RESET_BG_COLOR);
                }
            }
        }
    }

    public void printBlackGame(){
        for(int i = 0; i < 10; i++){

            if(i == 0 || i == 9){
                printBlackBorder();
                continue;
            }

            for(int j = 0; j < 10; j++){

                if(j == 0 || j == 9){
                    System.out.print(SET_BG_COLOR_LIGHT_GREY);
                    System.out.printf(" %d ",i);
                }
                else{
                    System.out.print("   ");
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
}
