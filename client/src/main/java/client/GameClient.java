package client;

import exception.DataAccessException;
import exception.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class GameClient {
    private String playerName = null;
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;

    public GameClient(String serverUrl) throws DataAccessException{
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println("♕ Welcome to chess. Type 'help' to get started.");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("quit")){
            printPrompt();
            String line = scanner.nextLine();

            try{
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        if(state == State.SIGNEDOUT){
            System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED_OUT] >>> " + SET_TEXT_COLOR_GREEN);
        }
        else{
            System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED_IN] >>> " + SET_TEXT_COLOR_GREEN);
        }
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
//                case "register" -> signIn(params);
                case "login" -> logIn(params);
//                case "list" -> listPets();
//                case "join" -> signOut();
//                case "observe" -> adoptPet(params);
//                case "logout" -> adoptAllPets();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String logIn(String... params) throws ResponseException {
        if (params.length >= 3) {
            state = State.SIGNEDIN;
            playerName = String.join("-", params);
            return String.format("You signed in as %s.", playerName);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                       register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                       login <USERNAME> <PASSWORD> - to log in an existing user
                       help - display list of available commands
                       quit - exit program
                    """;
        }
        return """
                   create <NAME> - create a new game
                   list - display a list of all available games
                   join <ID> [WHITE|BLACK] - join a game
                   observe <ID> - watch a game that is currently in play
                   logout - logs out current user
                   help - display list of available commands
                   quit - exit program
                """;
    }

}
