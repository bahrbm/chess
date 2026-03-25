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
        System.out.println("♕ Welcome to the game. Enter 'quit' to leave. ♕");
        printGame();

        Scanner scanner = new Scanner(System.in);
        String line = "";
        while(!line.equals("quit")){
            System.out.println("♕ Welcome to the game. Enter 'quit' to leave. ♕");
            printGame();
            line = scanner.nextLine();
        }
    }

    public void printGame(){

    }
}
