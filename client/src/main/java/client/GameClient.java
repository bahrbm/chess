package client;

import chess.*;
import exception.*;
import request.*;
import result.*;
import server.*;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import java.util.*;

import static ui.EscapeSequences.*;

public class GameClient implements NotificationHandler {
    private State state = State.SIGNEDOUT;
    private String playerName = null;
    private String authToken = null;
    private final ServerFacade server;
    private final Map<Integer, ImportantGameInfo> gameOrder = new HashMap<>();
    private final WebSocketFacade ws;
    private int gameID;
    private ChessGame.TeamColor team;

    public GameClient(String serverUrl) throws ResponseException {
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
        else if(state == State.SIGNEDIN){
            System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED_IN] >>> " + SET_TEXT_COLOR_GREEN);
        }
        else if(state == State.PLAYING){
            System.out.print("\n" + RESET_TEXT_COLOR + "[IN_GAME] >>> " + SET_TEXT_COLOR_GREEN);
        }
        else{
            System.out.print("\n" + RESET_TEXT_COLOR + "[OBSERVING] >>> " + SET_TEXT_COLOR_GREEN);
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
                case "leave" -> leaveGame();
                case "redraw" -> redrawBoard();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException | DataAccessException ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        if(state == State.SIGNEDOUT) {
            return SET_TEXT_COLOR_BLUE + """
                       register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                       login <USERNAME> <PASSWORD> - to log in an existing user
                       help - display list of available commands
                       quit - exit program
                    """;
        }
        else if(state == State.SIGNEDIN)
            return SET_TEXT_COLOR_BLUE + """
                       create <NAME> - create a new game
                       list - display a list of all available games
                       join <ID> [WHITE|BLACK] - join a game
                       observe <ID> - watch a game that is currently in play
                       logout - logs out current user
                       help - display list of available commands
                       quit - exit program
                    """;
        else{
            return SET_TEXT_COLOR_BLUE + """
                   redraw - redraw the current game
                   leave - leave game (allows someone else to take your place)
                   move <ROW> <COL> <ROW> <COL> - make a move
                   resign - surrender and end the game
                   highlight <ROW> <COL> - highlight all available moves for the current piece
                   help - lists out all available commands
                """;
        }
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
        assertSignedIn();
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
        assertSignedIn();

        if (params.length >= 1) {

            CreateGameRequest request = new CreateGameRequest(params[0]);
            server.createGame(request);

            return "Game Successfully created";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <NAME>");
    }

    public String listGames() throws DataAccessException, ResponseException {
        assertSignedIn();
        updateList();

        int index = 1;
        for(ImportantGameInfo game : gameOrder.values()){
            System.out.printf("%d - %s, white: %s, black: %s\n", index, game.gameName(), game.whiteUsername(), game.blackUsername());
            index += 1;
        }

        return "End of List";
    }

    public String joinGame(String... params) throws ResponseException, DataAccessException{
        assertSignedIn();

        if (params.length >= 2) {
            gameID = Integer.parseInt(params[0]);
            ImportantGameInfo game = gameOrder.get(gameID);
            String teamColor = params[1];

            assertCorrectTeam(teamColor);

            JoinGameRequest request = null;

            try{
                request = new JoinGameRequest(teamColor, gameID);
            }catch(NullPointerException ex){
                return "Game does not exist";
            }

            try{
                server.joinGame(request);
            }catch(DataAccessException ex){
                return ex.getMessage();
            }

            ws.enterGame(game.gameID(), authToken);
            state = State.PLAYING;

            return "";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <ID> [WHITE|BLACK]");
    }

    public String leaveGame() throws ResponseException, DataAccessException {
        assertInGame();

        server.leaveGame(new LeaveGameRequest(gameID));
        ws.leaveGame(gameID, authToken);
        gameID = -1;
        state = State.SIGNEDIN;

        return "";
    }

    public String redrawBoard() throws ResponseException, DataAccessException {
        assertInGame();

        // Updates the board
        updateList();

        printGame(gameOrder.get(gameID).currGame().getBoard());
        return "";
    }

    public String observe(String... params) throws ResponseException, DataAccessException{
        assertSignedIn();

        if (params.length >= 1) {
            gameID = Integer.parseInt(params[0]);
            ImportantGameInfo game = gameOrder.get(gameID);

            if(game == null){
                return "Game does not exist";
            }

            ws.enterGame(gameID, authToken);
            state = State.OBSERVING;

            return "";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <ID>");
    }

    private void assertSignedIn() throws ResponseException {
        if(state == State.SIGNEDOUT){
            throw new ResponseException(ResponseException.Code.ClientError, "You must sign in");
        }
    }

    private void assertInGame() throws ResponseException{
        if(state == State.SIGNEDIN || state == State.SIGNEDOUT){
            throw new ResponseException(ResponseException.Code.ClientError, "You are not in a game");
        }
    }

    private void assertPlaying() throws ResponseException {
        if(state != State.PLAYING){
            throw new ResponseException(ResponseException.Code.ClientError, "You are not a player");
        }
    }

    private void updateList() throws DataAccessException {
        ListGamesResult result = server.listGames(new ListGamesRequest());
        Collection<ImportantGameInfo> games = result.games();

        gameOrder.clear();

        int index = 1;
        for(ImportantGameInfo game : games){
            gameOrder.put(index, game);

            index +=1;
        }
    }

    private void assertCorrectTeam(String teamColor) throws ResponseException{
        if(Objects.equals(teamColor, "white")){
            team = ChessGame.TeamColor.WHITE;
        }
        else if(Objects.equals(teamColor, "black")){
            team = ChessGame.TeamColor.BLACK;
        }
        else{
            throw new ResponseException(ResponseException.Code.ClientError,"Invalid Team Color Chosen");
        }
    }

    public void printGame(ChessBoard currBoard){

        System.out.println("\n\n\n\n\n");

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

                    if(isBlank(currPos, currBoard)){
                        System.out.print("   ");
                    }
                    else if(isWhite(currPos, currBoard)){
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

    private boolean isBlank(ChessPosition currPos, ChessBoard currBoard){
        return currBoard.getPiece(currPos) == null;
    }

    private boolean isWhite(ChessPosition currPos, ChessBoard currBoard){
        return currBoard.getPiece(currPos).getTeamColor() == ChessGame.TeamColor.WHITE;
    }

    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(message.getMessage());
            case ERROR -> displayError(message.getMessage());
            case LOAD_GAME -> loadGame(((LoadGameMessage) message).getGame());
        }
    }

    public void displayNotification(String message){
        System.out.print(message);
        printPrompt();
    }

    public void displayError(String message){
        System.out.print("Error: " + message);
        printPrompt();
    }

    public void loadGame(ChessGame game){
        System.out.println("\n");
        printGame(game.getBoard());
        printPrompt();
    }
}
