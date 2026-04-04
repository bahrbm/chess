package client;

import chess.ChessGame;
import exception.*;
import request.*;
import result.ImportantGameInfo;
import result.ListGamesResult;
import server.NotificationHandler;
import server.ServerFacade;
import server.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.util.*;

import static ui.EscapeSequences.*;

public class GameClient implements NotificationHandler {
    private String playerName = null;
    private String authToken = null;
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private Map<Integer, ImportantGameInfo> gameOrder = new HashMap<>();
    private final WebSocketFacade ws;

    public GameClient(String serverUrl) throws DataAccessException, ResponseException {
        server = new ServerFacade(serverUrl);
        ws = new WebSocketFacade(serverUrl, this);
    }

    public void run() {
        System.out.println("♕ Welcome to chess ♕");
        System.out.print(help());

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
                case "register" -> register(params);
                case "login" -> login(params);
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observe(params);
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException | DataAccessException ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return SET_TEXT_COLOR_BLUE + """
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

    public String register(String... params) throws ResponseException, DataAccessException {
        if (params.length >= 3) {
            playerName = params[0];
            String password = params[1];
            String email = params[2];

            RegisterRequest request = new RegisterRequest(playerName, password, email);
            try{
                authToken = server.register(request);
                state = State.SIGNEDIN;
                return String.format("You signed in as %s.", playerName);
            }
            catch(DataAccessException ex){
                return ex.getMessage();
            }

        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String login(String... params) throws ResponseException, DataAccessException{
        if (params.length >= 2) {
            playerName = params[0];
            String password = params[1];

            LoginRequest request = new LoginRequest(playerName, password);

            try{
                authToken = server.login(request);
                state = State.SIGNEDIN;
                return String.format("You signed in as %s.", playerName);
            }
            catch(DataAccessException ex){
                return ex.getMessage();
            }

        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <USERNAME> <PASSWORD>");
    }

    public String logout() throws ResponseException, DataAccessException{
        LogoutRequest request = new LogoutRequest(authToken);

        try{
            server.logout(request);
            state = State.SIGNEDOUT;
            return "Successfully logged out";
        } catch(DataAccessException e) {
            return e.getMessage();
        }
    }

    public String createGame(String... params) throws DataAccessException, ResponseException {
        if (params.length >= 1) {

            CreateGameRequest request = new CreateGameRequest(params[0]);
            server.createGame(request);

            return "Game Successfully created";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <NAME>");
    }

    public String listGames() throws DataAccessException{
        ListGamesResult result = server.listGames(new ListGamesRequest());
        Collection<ImportantGameInfo> games = result.games();

        // Need to clear previous list to store most recent list
        gameOrder.clear();

        int index = 1;
        for(ImportantGameInfo game : games){
            gameOrder.put(index, game);

            System.out.printf("%d - %s, white: %s, black: %s\n", index, game.gameName(), game.whiteUsername(), game.blackUsername());

            index += 1;
        }

        return "End of List";
    }

    public String joinGame(String... params) throws ResponseException, DataAccessException{
        if (params.length >= 2) {
            ImportantGameInfo game = gameOrder.get(Integer.parseInt(params[0]));
            String team = params[1];

            JoinGameRequest request = null;

            try{
                request = new JoinGameRequest(team, game.gameID());
            }catch(NullPointerException ex){
                return "Game does not exist";
            }

            try{
                server.joinGame(request);
            }catch(DataAccessException ex){
                return ex.getMessage();
            }

            Repl currGame = new Repl();
            currGame.setCurrGame(game.currGame());

            if(Objects.equals(team, "white")){
                currGame.setTeam(ChessGame.TeamColor.WHITE);
            }
            else{
                currGame.setTeam(ChessGame.TeamColor.BLACK);
            }

            currGame.run();

            return "";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <ID> [WHITE|BLACK]");
    }

    public String observe(String... params) throws ResponseException, DataAccessException{
        if (params.length >= 1) {
            ImportantGameInfo game = gameOrder.get(Integer.parseInt(params[0]));

            if(game == null){
                return "Game does not exist";
            }

            Repl currGame = new Repl();
            currGame.setCurrGame(game.currGame());
            currGame.setTeam(ChessGame.TeamColor.WHITE);

            currGame.run();

            return "";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <ID>");
    }

    @Override
    public void notify(ServerMessage notification) {
        System.out.println(SET_TEXT_COLOR_RED + notification.getServerMessage());
        printPrompt();
    }
}
